package com.mlmfreya.freya2.config;

import com.mlmfreya.freya2.libs.KTTheme;
import com.mlmfreya.freya2.libs.config.KTIconsBaseSettings;
import com.mlmfreya.freya2.libs.config.KTThemeBaseConfig;
import com.mlmfreya.freya2.libs.config.KTThemeSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class ThemeConfig {
    @Bean("theme")
    @RequestScope
    public KTTheme theme(){
        return new KTTheme();
    }

    @Bean("settings")
    public KTThemeBaseConfig settings() {
        KTThemeSettings settings = new KTThemeSettings();
        return settings.config;
    }

    @Bean("iconsSettings")
    public KTIconsBaseSettings iconsSettings(){
       return new KTIconsBaseSettings();
    }
}
