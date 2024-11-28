package ma.bonmyd.backendincident.enums;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum EmailTemplateName {

    ACTIVATE_ACCOUNT("activate_account");
    private final String name;

    EmailTemplateName(String name) {
        this.name = name;
    }
}
