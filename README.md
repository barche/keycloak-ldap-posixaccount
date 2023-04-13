# keycloak-ldap-posixaccount

This is a Keycloak extension to automatically increment the POSIX account UID number (LDAP attribute uidNumber) when newly creating LDAP users. Also sets `homeDirectory` to `/home/username`.

## Usage

Run the maven package command:

```bash
mvn compile
mvn package
```

Next, copy the jar file from the `target` directory to the `/opt/keycloak/providers/` directory and restart Keycloak. You can then add a mapper in the LDAP user federation of the type `keycloak-ldap-posixaccount` and configure the first UID to use for new users.