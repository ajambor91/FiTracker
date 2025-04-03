path "secret/data/fit-app-main/jwks" {
  capabilities = ["read"]
}

path "transit/keys/jwt-rsa-key" {
  capabilities = ["read"]
}

path "transit/sign/jwt-rsa-key" {
  capabilities = ["create", "update"]
}

path "transit/verify/jwt-rsa-key" {
  capabilities = ["create", "update"]
}

path "transit/export/encryption-key/jwt-rsa-key" {
  capabilities = ["read"]
}