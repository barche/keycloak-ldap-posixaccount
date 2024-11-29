/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package be.ac.rma.hpc.mapper;

import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.ldap.LDAPConfig;
import org.keycloak.storage.ldap.LDAPStorageProvider;
import org.keycloak.storage.ldap.mappers.AbstractLDAPStorageMapper;
import org.keycloak.storage.ldap.mappers.AbstractLDAPStorageMapperFactory;
import org.keycloak.storage.ldap.mappers.LDAPConfigDecorator;

import java.util.List;

/**
 * @author Bart Janssens
 */
public class LdapPosixAccountFactory extends AbstractLDAPStorageMapperFactory implements LDAPConfigDecorator {

    public static final String PROVIDER_ID = "keycloak-ldap-posixaccount";
    protected static final List<ProviderConfigProperty> configProperties;

    static {
        List<ProviderConfigProperty> props = getConfigProps(null);
        configProperties = props;
    }

    static List<ProviderConfigProperty> getConfigProps(ComponentModel p) {

        ProviderConfigurationBuilder config = ProviderConfigurationBuilder.create()
                .property().name(LdapPosixAccount.LDAP_NEXT_UID)
                .label("Next POSIX UID")
                .helpText("Value for the POSIX UID number for the next new user. This should not refer to any existing user and will be incremented automatically with each newly added user.")
                .type(ProviderConfigProperty.STRING_TYPE)
                .add()
                .property().name(LdapPosixAccount.LDAP_DEFAULT_GID)
                .label("Fixed POSIX GID")
                .helpText("Value for the POSIX GID number for the new user. If set to 0, this mapper won' add GID, if set to non-zero value all users will be given this GID. If unset, user will get GID=UID")
                .type(ProviderConfigProperty.STRING_TYPE)
                .add()
                .property().name(LdapPosixAccount.LDAP_HOME_BASE)
                .label("Home basedir")
                .helpText("Basedir to be combined with username (default is " + LdapPosixAccount.LDAP_DEFAULT_HOME_BASEDIR + ")")
                .type(ProviderConfigProperty.STRING_TYPE)
                .add()
                ;
        return config.build();
    }

    @Override
    public String getHelpText() {
        return "Used to assign an auto-incrementing POSIX account UID number (LDAP attribute uidNumber) to newly created LDAP users. Also sets homeDirectory to e.g. /home/username and optionally GID (LDAP gidNumber). To work properly user should be mapped to a proper object class (posixAccount).";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel config) throws ComponentValidationException {
        checkMandatoryConfigAttribute(LdapPosixAccount.LDAP_NEXT_UID, "Next POSIX UID", config);
    }

    @Override
    protected AbstractLDAPStorageMapper createMapper(ComponentModel mapperModel, LDAPStorageProvider federationProvider) {
        return new LdapPosixAccount(mapperModel, federationProvider);
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties(RealmModel realm, ComponentModel parent) {
        return getConfigProps(parent);
    }

    @Override
    public void updateLDAPConfig(LDAPConfig ldapConfig, ComponentModel mapperModel) {
    }
}
