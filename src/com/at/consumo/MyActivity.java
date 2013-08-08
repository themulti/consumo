package com.at.consumo;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyActivity extends Activity implements View.OnClickListener {
    private static final int RESULT_SETTINGS = 1;
    Button button;
    private ConsumoSettings settings;
    Button aboutButton;
    Button refreshButton;
    //
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.layout.main, menu);
//        return true;
//    }
    private WebView webView;

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
        variables.put("form", form);

        return variables;
    }

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

        webView = (WebView) findViewById(R.id.webView);


        // Add more planets. If you passed a String[] instead of a List<String>
        // into the ArrayAdapter constructor, you must not add more items.
        // Otherwise an exception will occur.

        // Set the ArrayAdapter as the ListView's adapter.

        refreshButton = (Button) findViewById(R.id.refresh);
        refreshButton.setOnClickListener(this);
        settings = showUserSettings();

        Button settingButton = (Button) findViewById(R.id.setting);
        settingButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), UserSettingActivity.class);
                startActivity(intent);
            }
        });


        aboutButton = (Button) findViewById(R.id.about);
        aboutButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity.this);

                // Set Alert Dialog Title
                builder.setTitle("Consumo");

                // Set an Icon for this Alert Dialog
                //builder.setIcon(R.drawable.ic_launcher);


                // Set Alert Dialog Message
                builder.setMessage("\n\nAdvanced Technology®\n\n\nthemulti@gmail.com")

                        // Neautral button functionality
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int arg0) {
                                //Toast.makeText(AlertDialogActivity.this, "You clicked on OK", Toast.LENGTH_SHORT).show();
                                //Do more stuffs
                            }
                        });

                // Create the Alert Dialog
                AlertDialog alertdialog = builder.create();

                // Show Alert Dialog
                alertdialog.show();
            }
        });

    }

    private void update() {
        if (isEmptyOrNull(settings.getUsername()) || isEmptyOrNull(settings.getPassword())) {
            invalidUsernamePassword("Please set username/password!!");
        } else {
            Map<String, Object> maps = updateTime(settings.getUsername(), settings.getPassword());
            if (maps.isEmpty()) {
                sendErrorMessage("Ocorreu algum erro!!");
            } else if (maps.containsKey("usernamepassword")) {
                invalidUsernamePassword("Please set username/password!!");
            } else {
                webView.loadData(maps.get("detail").toString(), "text/html", "UTF-8");
            }

            //String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        }
    }

    private boolean isEmptyOrNull(String s) {
        return (s == null || s.trim().length() == 0);
    }

    private void sendErrorMessage(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity.this);
        // Set Alert Dialog Title
        builder.setTitle("Error Message");

        // Set Alert Dialog Message
        builder.setMessage(s)
                // Neautral button functionality
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg0) {
                    }
                });

        // Create the Alert Dialog
        AlertDialog alertdialog = builder.create();

        // Show Alert Dialog
        alertdialog.show();
    }

    private void invalidUsernamePassword(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity.this);
        // Set Alert Dialog Title
        builder.setTitle("Error Message");

        // Set Alert Dialog Message
        builder.setMessage(s)
                // Neautral button functionality
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg0) {
                        Intent intent = new Intent(getBaseContext(), UserSettingActivity.class);
                        startActivity(intent);
                    }
                });

        // Create the Alert Dialog
        AlertDialog alertdialog = builder.create();

        // Show Alert Dialog
        alertdialog.show();
    }

    private Map<String, Object> updateTime(String login1, String login2) {
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

            if (str.toString().indexOf("Introduza-os") > 0) {
                Map<String, Object> variables = new HashMap<String, Object>();
                variables.put("usernamepassword", "0");
                return variables;
            } else {
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
            StringBuilder strDetail = getResponseString(httpConnection);

            variables.put("detail", strDetail);

            TagParser td = new TagParser("td", strDetail.toString(), strDetail.indexOf("CONSUMO HAPPY HOURS"));
            String v = td.parse();

            //String [][] array = new String[][];
            List<String[]> list = new ArrayList<String[]>();

            while (!v.equalsIgnoreCase("TOTAL")) {
                String[] array1 = {v, td.parse(), td.parse(), td.parse()};
                v = td.parse();
                //System.out.println("array = " + Arrays.toString( array1));
                list.add(array1);
            }

            variables.put("month", list);


            return variables;
            }


            //button.setText(variables.get("third"));


        } catch (Exception e) {
            e.printStackTrace();
            //button.setText(e.getMessage());
            return new HashMap<String, Object>();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings: {
                Intent i = new Intent(this, UserSettingActivity.class);
                startActivityForResult(i, RESULT_SETTINGS);
            }
            break;
            case R.id.menu_about: {
                aboutButton.performClick();
            }
            break;

            case R.id.menu_refresh: {
                refreshButton.performClick();
            }
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


        return new ConsumoSettings(username, password);
    }

    private static class Tuple {
        public int lastPosition;
        public String value;

        private Tuple(int lastPosition, String value) {
            this.lastPosition = lastPosition;
            this.value = value;
        }
    }


    //    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//    }
}
