# Production Deployment Guide

For publishing your Twitter-like app, here are the **best production deployment options**:

## 🚀 **Option 1: Cloud Deployment with Docker (Recommended)**

### **AWS Deployment**
```bash
# 1. Build and push to AWS ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com

# Build and tag
docker build -t pulsefeed-backend ./backend
docker tag pulsefeed-backend:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/pulsefeed-backend:latest

# Push to ECR
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/pulsefeed-backend:latest

# 2. Deploy with ECS or EKS
# Use AWS RDS for PostgreSQL
# Use AWS ElastiCache for Redis
```

### **Google Cloud Platform**
```bash
# 1. Build and push to Google Container Registry
gcloud auth configure-docker
docker build -t gcr.io/your-project-id/pulsefeed-backend ./backend
docker push gcr.io/your-project-id/pulsefeed-backend

# 2. Deploy to Google Cloud Run
gcloud run deploy pulsefeed-backend \
  --image gcr.io/your-project-id/pulsefeed-backend \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated

# Use Cloud SQL for PostgreSQL
# Use Memorystore for Redis
```

### **DigitalOcean App Platform**
```yaml
# app.yaml
name: pulsefeed
services:
- name: backend
  source_dir: /backend
  github:
    repo: your-username/pulsefeed
    branch: main
  run_command: ./main
  environment_slug: go
  instance_count: 1
  instance_size_slug: basic-xxs
  envs:
  - key: DATABASE_URL
    value: ${db.DATABASE_URL}
  - key: REDIS_URL
    value: ${redis.DATABASE_URL}
  - key: JWT_SECRET
    value: ${JWT_SECRET}

databases:
- name: db
  engine: PG
  version: "15"
- name: redis
  engine: REDIS
  version: "7"
```

## 🐳 **Option 2: VPS with Docker Compose**

### **Setup on Ubuntu VPS**
```bash
# 1. Install Docker on VPS
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# 2. Clone your repository
git clone https://github.com/your-username/pulsefeed.git
cd pulsefeed

# 3. Create production environment
cp .env.example .env
# Edit .env with production values

# 4. Deploy with Docker Compose
docker-compose -f docker-compose.prod.yml up -d

# 5. Setup reverse proxy with Nginx
sudo apt install nginx certbot python3-certbot-nginx
sudo certbot --nginx -d yourdomain.com
```

### **Production Docker Compose**
```yaml
# docker-compose.prod.yml
services:
  postgres:
    image: postgres:15-alpine
    container_name: pulsefeed-postgres
    environment:
      POSTGRES_DB: pulsefeed
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    restart: unless-stopped
    networks:
      - pulsefeed-network

  redis:
    image: redis:7-alpine
    container_name: pulsefeed-redis
    command: redis-server --requirepass ${REDIS_PASSWORD}
    volumes:
      - redis_data:/data
    restart: unless-stopped
    networks:
      - pulsefeed-network

  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile.prod
    container_name: pulsefeed-backend
    environment:
      DATABASE_URL: postgres://${DB_USER}:${DB_PASSWORD}@postgres:5432/pulsefeed?sslmode=disable
      REDIS_URL: redis://:${REDIS_PASSWORD}@redis:6379
      JWT_SECRET: ${JWT_SECRET}
      PORT: 8080
    ports:
      - "8080:8080"
    depends_on:
      - postgres
      - redis
    restart: unless-stopped
    networks:
      - pulsefeed-network

volumes:
  postgres_data:
  redis_data:

networks:
  pulsefeed-network:
    driver: bridge
```

## 📱 **Option 3: Serverless Deployment**

### **Vercel + PlanetScale + Upstash**
```bash
# 1. Deploy backend to Vercel
npm i -g vercel
vercel --prod

# 2. Use PlanetScale for PostgreSQL
pscale auth login
pscale database create pulsefeed --region us-east

# 3. Use Upstash for Redis
# Create Redis database at upstash.com
```

### **Netlify Functions + Supabase**
```bash
# 1. Deploy to Netlify
netlify deploy --prod

# 2. Use Supabase for PostgreSQL
# Create project at supabase.com
# Get connection string

# 3. Use Upstash Redis
# Add environment variables in Netlify
```

## 🌐 **Option 4: Kubernetes Deployment**

### **Kubernetes Manifests**
```yaml
# k8s/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: pulsefeed-backend
spec:
  replicas: 3
  selector:
    matchLabels:
      app: pulsefeed-backend
  template:
    metadata:
      labels:
        app: pulsefeed-backend
    spec:
      containers:
      - name: backend
        image: your-registry/pulsefeed-backend:latest
        ports:
        - containerPort: 8080
        env:
        - name: DATABASE_URL
          valueFrom:
            secretKeyRef:
              name: pulsefeed-secrets
              key: database-url
        - name: REDIS_URL
          valueFrom:
            secretKeyRef:
              name: pulsefeed-secrets
              key: redis-url
---
apiVersion: v1
kind: Service
metadata:
  name: pulsefeed-service
spec:
  selector:
    app: pulsefeed-backend
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer
```

## 📊 **Recommended Architecture for Production**

### **Small Scale (< 1000 users)**
- **Backend**: Single VPS with Docker Compose
- **Database**: Managed PostgreSQL (DigitalOcean, AWS RDS)
- **Cache**: Managed Redis (AWS ElastiCache, Upstash)
- **CDN**: Cloudflare for static assets
- **Cost**: ~$20-50/month

### **Medium Scale (1K-10K users)**
- **Backend**: Cloud Run, App Engine, or ECS
- **Database**: Managed PostgreSQL with read replicas
- **Cache**: Redis cluster
- **Load Balancer**: Cloud load balancer
- **CDN**: CloudFront or Cloudflare
- **Cost**: ~$100-300/month

### **Large Scale (10K+ users)**
- **Backend**: Kubernetes cluster with auto-scaling
- **Database**: PostgreSQL cluster with sharding
- **Cache**: Redis cluster with sentinel
- **Message Queue**: RabbitMQ or AWS SQS
- **Monitoring**: Prometheus + Grafana
- **Cost**: ~$500+/month

## 🔧 **Production Optimizations**

### **Backend Optimizations**
```dockerfile
# Dockerfile.prod
FROM golang:1.21-alpine AS builder
WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download
COPY . .
RUN CGO_ENABLED=0 GOOS=linux go build -ldflags="-s -w" -o main .

FROM alpine:latest
RUN apk --no-cache add ca-certificates tzdata
WORKDIR /root/
COPY --from=builder /app/main .
COPY --from=builder /app/uploads ./uploads
EXPOSE 8080
CMD ["./main"]
```

### **Database Optimizations**
```sql
-- Add indexes for better performance
CREATE INDEX idx_posts_user_id ON posts(user_id);
CREATE INDEX idx_posts_created_at ON posts(created_at DESC);
CREATE INDEX idx_likes_post_id ON likes(post_id);
CREATE INDEX idx_likes_user_id ON likes(user_id);
CREATE INDEX idx_follows_follower_id ON follows(follower_id);
CREATE INDEX idx_follows_following_id ON follows(following_id);

-- Enable connection pooling
ALTER SYSTEM SET max_connections = 200;
ALTER SYSTEM SET shared_buffers = '256MB';
ALTER SYSTEM SET effective_cache_size = '1GB';
```

### **Security Configurations**
```bash
# Environment variables for production
JWT_SECRET=your-super-secure-256-bit-secret-key
DATABASE_URL=postgres://user:password@host:5432/pulsefeed?sslmode=require
REDIS_URL=redis://:password@host:6379
CORS_ORIGINS=https://yourdomain.com,https://www.yourdomain.com
RATE_LIMIT=100
```

## 📱 **Android App Publishing**

### **Google Play Store**
```bash
# 1. Generate signed APK
./gradlew assembleRelease

# 2. Create Play Console account
# 3. Upload APK and fill store listing
# 4. Set up app signing
# 5. Release to internal testing first
```

### **App Store Connect (iOS version)**
```bash
# If you want iOS version later
# 1. Convert to Flutter or React Native
# 2. Or create separate iOS app
```

## 🚀 **Quick Start for Production**

### **Fastest Production Setup (Recommended)**
```bash
# 1. Use DigitalOcean App Platform
# - Fork your GitHub repo
# - Connect to DigitalOcean App Platform
# - Add managed PostgreSQL and Redis
# - Deploy automatically

# 2. Or use Railway
railway login
railway new
railway add postgresql redis
railway up
```

### **Most Scalable Setup**
```bash
# 1. AWS with Terraform
terraform init
terraform plan
terraform apply

# 2. Kubernetes with Helm
helm install pulsefeed ./helm-chart
```

Choose the deployment method based on your:
- **Budget**: VPS ($20/month) vs Cloud ($100+/month)
- **Scale**: Small (VPS) vs Large (Kubernetes)
- **Complexity**: Simple (App Platform) vs Advanced (K8s)
- **Control**: Managed (Cloud) vs Self-hosted (VPS)

All methods will give you a production-ready Twitter-like app that can handle real users!
