#!/bin/sh
set -eu

cd "$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"

compose() {
    docker compose -f docker-compose.deploy.yml "$@"
}

for artifact in app.jar ../ForumWebSite/dist/index.html ../ForumAdmin/dist/index.html; do
    if [ ! -s "$artifact" ]; then
        echo "Deployment artifact is missing or empty: $artifact" >&2
        exit 1
    fi
done

compose config --quiet
compose up -d --build --remove-orphans

# Nginx resolves the app service when its configuration is loaded. Refresh it
# after an app-only replacement may have changed the container address.
compose exec -T nginx nginx -t
compose exec -T nginx nginx -s reload

# Verify both frontends are included in this release.
compose exec -T nginx wget -q -T 5 -O /dev/null http://127.0.0.1/
compose exec -T nginx wget -q -T 5 -O /dev/null http://127.0.0.1/console/

# Check the full HTTP path, not just the static index health check.
for endpoint in introduction releases; do
    attempt=0
    until compose exec -T nginx wget -q -T 5 -O /dev/null \
        "http://127.0.0.1/api/website/$endpoint"; do
        attempt=$((attempt + 1))
        if [ "$attempt" -ge 60 ]; then
            echo "Deployment failed: /api/website/$endpoint is unavailable." >&2
            compose logs --tail=80 app nginx
            exit 1
        fi
        sleep 2
    done
done

compose ps
echo "Deployment verified: both frontends and public website APIs are reachable."
