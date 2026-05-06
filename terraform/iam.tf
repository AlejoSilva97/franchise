# Rol de ejecucion: permite a ECS descargar imagen de ECR y escribir logs
resource "aws_iam_role" "ecs_task_execution" {
  name = "${var.app_name}-ecs-execution-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Action    = "sts:AssumeRole"
      Principal = { Service = "ecs-tasks.amazonaws.com" }
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution" {
  role       = aws_iam_role.ecs_task_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# Rol de tarea: permisos que tiene la APP en tiempo de ejecucion (DynamoDB)
resource "aws_iam_role" "ecs_task" {
  name = "${var.app_name}-ecs-task-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Action    = "sts:AssumeRole"
      Principal = { Service = "ecs-tasks.amazonaws.com" }
    }]
  })
}

resource "aws_iam_role_policy" "dynamodb_access" {
  name = "${var.app_name}-dynamodb-policy"
  role = aws_iam_role.ecs_task.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Action = [
        "dynamodb:PutItem",
        "dynamodb:GetItem",
        "dynamodb:DeleteItem",
        "dynamodb:Query",
        "dynamodb:UpdateItem"
      ]
      Resource = [
        "arn:aws:dynamodb:${var.aws_region}:*:table/${var.dynamodb_table_franchises}",
        "arn:aws:dynamodb:${var.aws_region}:*:table/${var.dynamodb_table_franchises}/index/*",
        "arn:aws:dynamodb:${var.aws_region}:*:table/${var.dynamodb_table_branches}",
        "arn:aws:dynamodb:${var.aws_region}:*:table/${var.dynamodb_table_branches}/index/*",
        "arn:aws:dynamodb:${var.aws_region}:*:table/${var.dynamodb_table_products}",
        "arn:aws:dynamodb:${var.aws_region}:*:table/${var.dynamodb_table_products}/index/*"
      ]
    }]
  })
}