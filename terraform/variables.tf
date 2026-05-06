variable "aws_region" {
  description = "AWS region donde se despliega todo"
  type        = string
  default     = "us-east-1"
}

variable "app_name" {
  description = "Nombre de la aplicacion"
  type        = string
  default     = "franchise"
}

variable "app_port" {
  description = "Puerto que expone el contenedor"
  type        = number
  default     = 8080
}

variable "ecr_image_tag" {
  description = "Tag de la imagen Docker a desplegar"
  type        = string
  default     = "latest"
}

variable "task_cpu" {
  description = "CPU units para la tarea Fargate (256 = 0.25 vCPU)"
  type        = number
  default     = 256
}

variable "task_memory" {
  description = "Memoria en MB para la tarea Fargate"
  type        = number
  default     = 512
}

variable "dynamodb_table_franchises" {
  type    = string
  default = "franchises"
}

variable "dynamodb_table_branches" {
  type    = string
  default = "branches"
}

variable "dynamodb_table_products" {
  type    = string
  default = "products"
}