# ──────────────────────────────────────────────
# Tabla: franchises
# ──────────────────────────────────────────────
resource "aws_dynamodb_table" "franchises" {
  name         = var.dynamodb_table_franchises
  billing_mode = "PROVISIONED"
  read_capacity  = 5
  write_capacity = 5
  hash_key     = "id"

  attribute {
    name = "id"
    type = "S"
  }
}

# ──────────────────────────────────────────────
# Tabla: branches
# ──────────────────────────────────────────────
resource "aws_dynamodb_table" "branches" {
  name         = var.dynamodb_table_branches
  billing_mode = "PROVISIONED"
  read_capacity  = 5
  write_capacity = 5
  hash_key     = "id"

  attribute {
    name = "id"
    type = "S"
  }

  attribute {
    name = "franchiseId"
    type = "S"
  }

  global_secondary_index {
    name            = "franchiseId-index"
    hash_key        = "franchiseId"
    projection_type = "ALL"
    read_capacity   = 5
    write_capacity  = 5
  }
}

# ──────────────────────────────────────────────
# Tabla: products
# ──────────────────────────────────────────────
resource "aws_dynamodb_table" "products" {
  name         = var.dynamodb_table_products
  billing_mode = "PROVISIONED"
  read_capacity  = 5
  write_capacity = 5
  hash_key     = "id"

  attribute {
    name = "id"
    type = "S"
  }

  attribute {
    name = "branchId"
    type = "S"
  }

  attribute {
    name = "stock"
    type = "N"
  }

  global_secondary_index {
    name            = "branchId-stock-index"
    hash_key        = "branchId"
    range_key       = "stock"
    projection_type = "ALL"
    read_capacity   = 5
    write_capacity  = 5
  }
}