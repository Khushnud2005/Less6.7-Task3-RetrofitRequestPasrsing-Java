package uz.example.less67_task3_retrofitrequestpasrsing_java.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHttp {
public static boolean IS_TESTER = true;
    private static String  SERVER_DEVELOPMENT = "https://jsonplaceholder.typicode.com/";
    private static String SERVER_PRODUCTION = "https://jsonplaceholder.typicode.com/";
    static String server() {
        if (IS_TESTER)
            return SERVER_DEVELOPMENT;
        return SERVER_PRODUCTION;
    }
    static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(server())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static PosterService posterService = retrofit.create(PosterService.class);









}