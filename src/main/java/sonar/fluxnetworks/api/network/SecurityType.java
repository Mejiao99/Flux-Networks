package sonar.fluxnetworks.api.network;

import sonar.fluxnetworks.api.text.FluxTranslate;

import javax.annotation.Nonnull;

public enum SecurityType {
    PUBLIC(FluxTranslate.PUBLIC);


    private final FluxTranslate localization;

    SecurityType(FluxTranslate localization) {
        this.localization = localization;
    }

    @Nonnull
    public String getName() {
        return localization.t();
    }
}
