package com.at.consumo;


import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;

import android.view.View;
import android.view.Window;
import android.widget.*;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.text.DateFormat;
import java.util.*;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
public class MyActivity extends Activity implements View.OnClickListener {
    Button button;



    private GridView mainListView ;
    private ListAdapter listAdapter ;

    private ConsumoSettings settings;
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.layout.main, menu);
//        return true;
//    }


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //updateTime1();
        requestWindowFeature(Window.FEATURE_ACTION_BAR);

        ActionBar actionBar = getActionBar();
        setContentView(R.layout.main);

        // Find the ListView resource.
        mainListView = (GridView) findViewById( R.id.gridView );



        listAdapter = new ListAdapter(this, R.layout.simplerow);

        // Add more planets. If you passed a String[] instead of a List<String>
        // into the ArrayAdapter constructor, you must not add more items.
        // Otherwise an exception will occur.

        // Set the ArrayAdapter as the ListView's adapter.
        mainListView.setAdapter( listAdapter );

        Button refreshButton = (Button) findViewById(R.id.refresh);
        refreshButton.setOnClickListener(this);
        settings = showUserSettings();

        Button settingButton = (Button) findViewById(R.id.setting);
        settingButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), UserSettingActivity.class);
                startActivity(intent);
            }
        });


    }

    private  void update(){
        // Create and populate a List of planet names.

        listAdapter.clear();

        //Map<String, String> maps = updateTime("44842", "TheMulti0102");
        Map<String, Object> maps = updateTime(settings.getUsername(), settings.getPassword());

        // Create ArrayAdapter using the planet list.
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());


        if (maps != null){
            listAdapter.add(new DescriptionData("7h as 24h\t:", maps.get("first").toString()) );
            listAdapter.add(new DescriptionData("Happy Hours\t:", maps.get("second").toString()) );
            listAdapter.add(new DescriptionData("Restante\t:", maps.get("third").toString()) );
            listAdapter.add(new DescriptionData("Excesso\t:", maps.get("fourth").toString()) );
            listAdapter.add(new DescriptionData("Data Hora\t:",  currentDateTimeString));
        }else{
            listAdapter.add(new DescriptionData("Erro :", "null") );
        }

    }



    private static StringBuilder getResponseString(HttpURLConnection httpConnection) throws IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
        StringBuilder str = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            str.append(line);
        }
        rd.close();
        return str;
    }

    private static Map<String, String> getViewstate(HttpURLConnection httpConnection) throws IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
        StringBuilder str = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            str.append(line);
        }
//        System.out.println("------------" + str);
//        System.out.println("=============" + str.indexOf("Efectue o seu login para aceder a todas as funcionalidades"));
        return getViewstate(str.toString());
    }

    private static Map<String, String> getViewstate(String str) {
        Map<String, String> maps = new HashMap<String, String>();

        String[] variables = {"__EVENTTARGET", "__EVENTARGUMENT", "__VIEWSTATE", "__EVENTVALIDATION"};
        String valuepart = "value=\"";

//        System.out.println("str = " + str);


        for (int i = 0; i < variables.length; i++) {
            String variable = variables[i];


            int index = str.indexOf(variable);
            String value;
            if (index < 0) {
                value = "";
            } else {
                int indexValue = str.indexOf(valuepart, index + variable.length());
                int indexEnd = str.indexOf("\"", indexValue + valuepart.length());
                value = str.substring(indexValue + valuepart.length(), indexEnd);
            }
            maps.put(variable, value);

        }


        return maps;
    }


    private Map<String, Object> updateTime(String login1, String login2) {
//        button.setText(new Date().toString());
        try {
            String charset = "UTF-8";

            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));


            Map<String, String> maps = new HashMap<String, String>();
            maps.put("txtInputLogin1", login1);
//            maps.put("txtInputLogin1", "1111");
            maps.put("txtInputLogin2", login2);
//            maps.put("txtInputLogin2", "1111111");
            maps.put("_ButtonLogin", "");

            String loginPage = "http://portaldocliente.tvcabo.co.mz/HomePortalDoCliente.aspx?ReturnUrl=%2fConsumos.aspx";

            URL loginUrl = new URL(loginPage);


            HttpURLConnection httpConnection = (HttpURLConnection) loginUrl.openConnection();

//            httpConnection.setInstanceFollowRedirects(true);
            httpConnection.setRequestProperty("Connection", "Keep-Alive");
            //String cookies = httpConnection.getHeaderField("Set-Cookie");


            Map<String, String> formFields = getViewstate(httpConnection);


            maps.putAll(formFields);


            httpConnection = (HttpURLConnection) loginUrl.openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:20.0) Gecko/20100101 Firefox/20.0");
            httpConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            httpConnection.setRequestProperty("Accept-Charset", charset);
            httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);
            String and = "";
            StringBuilder query = new StringBuilder();
            for (Map.Entry<String, String> entry : maps.entrySet()) {
                query.append(and);
                query.append(URLEncoder.encode(entry.getKey(), charset) + "=" + URLEncoder.encode(entry.getValue(), charset));
                and = "&";
            }

            DataOutputStream output = null;
            try {
                output = new DataOutputStream(httpConnection.getOutputStream());
                output.writeBytes(query.toString());
                output.flush();
            } finally {
                if (output != null) try {
                    output.close();
                } catch (IOException logOrIgnore) {
                    logOrIgnore.printStackTrace();
                }
            }


            // Get the response
            StringBuilder str = getResponseString(httpConnection);

            URL consumoUrl = new URL("http://portaldocliente.tvcabo.co.mz/Consumos.aspx");
            httpConnection = (HttpURLConnection) consumoUrl.openConnection();
            httpConnection.setRequestMethod("GET");


            StringBuilder str2 = getResponseString(httpConnection);

            Map<String, Object> variables = fetchVariables(str2.toString());

            String form = variables.get("form").toString();
            String[] array = form.replaceAll("\'", "").split(",");
            for (int i = 0; i < array.length; i++) {
                array[i] = URLEncoder.encode(array[i], charset);
            }
            String url = String.format("http://portaldocliente.tvcabo.co.mz/ConsumosForm.aspx?nameForm=%s&idconta=%s&idPC=%s&DataIni=%s&DataFim=%s&MAC=%s", array);
            URL detalhes = new URL(url);
            httpConnection = (HttpURLConnection) detalhes.openConnection();
            httpConnection.setRequestMethod("GET");
            StringBuilder strDetalhes = getResponseString(httpConnection);

            TagParser td = new TagParser("td", strDetalhes.toString(), strDetalhes.indexOf("CONSUMO HAPPY HOURS"));
            String v = td.parse();

            //String [][] array = new String[][];
            List<String[]>  list = new ArrayList<String[]>();

            while (!v.equalsIgnoreCase("TOTAL")){
                String [] array1 = {v, td.parse(), td.parse(), td.parse()};
                v = td.parse();
                //System.out.println("array = " + Arrays.toString( array1));
                list.add(array1);
            }

            variables.put("month", list);




            return variables;

            //button.setText(variables.get("third"));


        } catch (Exception e) {
            //button.setText(e.getMessage());
            return null;
            //e.printStackTrace();
        }





    }

    private static Map<String, Object> fetchVariables(String page) {
        Map<String, Object> variables = new HashMap<String, Object>();

        int indexExcesso = page.indexOf("EXCESSO");
        String consumojavascript = "javascript:ConsumosForm(";
        int indexConsumoForm = page.indexOf(consumojavascript, indexExcesso);
        int indexEndConsumoForm = page.indexOf(");\"", indexConsumoForm + consumojavascript.length());
        String form = page.substring(indexConsumoForm + consumojavascript.length(), indexEndConsumoForm);
        String[] split = form.split(",");
        String start = split[3];
//        System.out.println("inicio = " + start);
        String end = split[4];
//        System.out.println("fim = " + end);

        variables.put("start", start);
        variables.put("end", end);

        TagParser td = new TagParser("td", page, indexConsumoForm);

        variables.put("first", td.parse());
        variables.put("second", td.parse());
        variables.put("third", td.parse());
        variables.put("fourth", td.parse());

        return variables;
    }



    private static class Tuple {
        public int lastPosition;
        public String value;

        private Tuple(int lastPosition, String value) {
            this.lastPosition = lastPosition;
            this.value = value;
        }
    }



    @Override
    public void onClick(View view) {
        update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }
    private static final int RESULT_SETTINGS = 1;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_settings:
                Intent i = new Intent(this, UserSettingActivity.class);
                startActivityForResult(i, RESULT_SETTINGS);
                break;

        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SETTINGS:
                settings = showUserSettings();
                break;

        }

    }

    private ConsumoSettings showUserSettings() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        StringBuilder builder = new StringBuilder();


        String username = sharedPrefs.getString("prefUsername", "NULL");
        String password = sharedPrefs.getString("prefPassword", "NULL");
        builder.append("\n Username: " + username);
        builder.append("\n Password: " + password.replaceAll(".", "*"));

//        builder.append("\n Send report:"
//                + sharedPrefs.getBoolean("prefSendReport", false));

//        builder.append("\n Sync Frequency: "
//                + sharedPrefs.getString("prefSyncFrequency", "NULL"));

        TextView settingsTextView = (TextView) findViewById(R.id.textUserSettings);

        settingsTextView.setText(builder.toString());

        return new ConsumoSettings(username, password);
    }


    //    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//    }
}
