package com.example.aplicacion_cvr.Api;

import com.example.aplicacion_cvr.Entidades_Api.Empresa;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
public interface ApiSunat {
    @GET("{id}?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InNhbHZhMjIwNTAyQGdtYWlsLmNvbSJ9.Mv_FTCBFiU8QLZUI2091SBsY9VPWubZ1UJCcq0u78Pc")  // Se añade el parámetro dinámico en la URL
    Call<Empresa> getDetallesConRuc(@Path("id") String id);
}
