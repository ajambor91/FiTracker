FROM hashicorp/vault:1.13.3

ENV VAULT_ADDR="http://0.0.0.0:8200"
ENV VAULT_API_ADDR="http://0.0.0.0:8200"
ENV VAULT_ADDRESS="http://0.0.0.0:8200"
ENV VAULT_SKIP_VERIFY="true"
#ENV VAULT_TOKEN="hvs.oPIe2ZlbdUV34ARajollap7Z"
EXPOSE 8200
EXPOSE 8201

ENTRYPOINT ["vault", "server", "-config=/vault/config/config.hcl"]
