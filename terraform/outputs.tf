output "alb_url" {
  description = "URL publica de la aplicacion"
  value       = "http://${aws_lb.app.dns_name}"
}

output "ecr_repository_url" {
  description = "URL del repositorio ECR para hacer push de la imagen"
  value       = aws_ecr_repository.app.repository_url
}

output "ecs_cluster_name" {
  value = aws_ecs_cluster.app.name
}