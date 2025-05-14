package com.example.aplicacion_cvr.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class ConfiguracionApiSunat {
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://dniruc.apisperu.com/api/v1/ruc/") // Reemplaza con la URL base de tu API
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
