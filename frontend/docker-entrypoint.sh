#!/bin/sh

# Replace environment variables in nginx config if needed
# This allows for runtime configuration

# If environment variables are set, replace them in static files
if [ -n "$REACT_APP_API_BASE_URL" ]; then
    echo "Setting API base URL to: $REACT_APP_API_BASE_URL"
    find /usr/share/nginx/html -name "*.js" -exec sed -i "s|REACT_APP_API_BASE_URL_PLACEHOLDER|$REACT_APP_API_BASE_URL|g" {} \;
fi

if [ -n "$REACT_APP_WEBSOCKET_URL" ]; then
    echo "Setting WebSocket URL to: $REACT_APP_WEBSOCKET_URL"
    find /usr/share/nginx/html -name "*.js" -exec sed -i "s|REACT_APP_WEBSOCKET_URL_PLACEHOLDER|$REACT_APP_WEBSOCKET_URL|g" {} \;
fi

# Start nginx
exec "$@"
