# Simple Docker configuration for React frontend
# This builds and serves the React application

# Use Node.js base image
FROM node:18

# Set working directory inside container
WORKDIR /app

# Copy package files first (for better caching)
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application
COPY . .

# Build the React application
RUN npm run build

# Install a simple web server to serve the built files
RUN npm install -g serve

# Expose the port the app runs on
EXPOSE 3000

# Serve the built application
CMD ["serve", "-s", "build", "-l", "3000"]
