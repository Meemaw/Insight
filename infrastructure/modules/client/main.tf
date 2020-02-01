resource "aws_s3_bucket" "static" {
  bucket = var.bucket_name

  tags = {
    environment = var.environment
  }
}
