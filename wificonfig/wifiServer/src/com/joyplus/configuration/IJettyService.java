//========================================================================
//$Id: IJettyService.java 474 2012-01-23 03:07:14Z janb.webtide $
//Copyright 2008 Mort Bay Consulting Pty. Ltd.
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at 
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//========================================================================

package com.joyplus.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.jetty.http.HttpGenerator;
import org.eclipse.jetty.http.ssl.SslContextFactory;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSocketConnector;
import org.eclipse.jetty.util.security.Credential;
import org.mortbay.ijetty.deployer.AndroidContextDeployer;
import org.mortbay.ijetty.deployer.AndroidWebAppDeployer;
import org.mortbay.ijetty.handler.DefaultHandler;
import org.mortbay.ijetty.util.IJettyToast;
import org.mortbay.ijetty.util.Log;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;

/**
 * IJettyService
 *
 * Android Service which runs the Jetty server, maintaining it in the active Notifications so that
 * the user can return to the IJetty Activity to control it at any time.
 */
public class IJettyService extends Service
{
    private static final String TAG = "Jetty";
    
    private static Resources __resources;
    private static final String CONTENT_RESOLVER_ATTRIBUTE = "org.mortbay.ijetty.contentResolver";
    private static final String ANDROID_CONTEXT_ATTRIBUTE = "org.mortbay.ijetty.context"; 
    
    public static final int __START_PROGRESS_DIALOG = 0;
    public static final int __STARTED = 0;
    public static final int __NOT_STARTED = 1;
    public static final int __STOPPED = 2;
    public static final int __NOT_STOPPED = 3;
    public static final int __STARTING = 4;
    public static final int __STOPPING = 5;
    
    public static final String __CONSOLE_PWD_DEFAULT = "admin";
    
    public static final String __WEBAPP_DIR = "webapps";
    public static final String __ETC_DIR = "etc";
    public static final String __CONTEXTS_DIR = "contexts";

    public static final String __TMP_DIR = "tmp";
    public static final String __WORK_DIR = "work";
    
    public static final String[] __configurationClasses = 
        new String[]
        {
            "org.mortbay.ijetty.webapp.AndroidWebInfConfiguration",
            "org.eclipse.jetty.webapp.WebXmlConfiguration",
            "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
            "org.eclipse.jetty.webapp.TagLibConfiguration" 
        };
    
    private static boolean __isRunning;
 
    private NotificationManager mNM;
    private Server server;
    private ContextHandlerCollection contexts;
    private boolean _useNIO;
    private boolean _useSSL;
    private int _port;
    private int _sslPort;
    private String _consolePassword;
    private String _keymgrPassword;
    private String _keystorePassword;
    private String _truststorePassword;
    private String _keystoreFile;
    private String _truststoreFile;
    private SharedPreferences preferences;
    private PackageInfo pi;
    private android.os.Handler _handler;

    private PowerManager.WakeLock wakeLock;
    private final IBinder binder = new LocalBinder();

    public static File __JETTY_DIR;
    
    static 
    {
        __isRunning = false;
    }
    
    
    /**
     * IJettyService always runs in-process with the IJetty activity.
     */
    public class LocalBinder extends Binder {
        IJettyService getService() {
            // Return this instance of LocalService so clients can call public methods
            return IJettyService.this;
        }
    }
    
    
    
    /**
     * JettyStarterThread
     *
     *
     */
    public class JettyStarterThread extends Thread
    {
        android.os.Handler _handler;
        
        public JettyStarterThread(android.os.Handler handler)
        {
            _handler = handler;
        }
        public void run ()
        {
            try
            {
                sendMessage(__STARTING);
                startJetty();
                sendMessage(__STARTED);
              
                Log.i(TAG, "Jetty started");
            }
            catch (Exception e)
            {
                sendMessage(__NOT_STARTED);
                Log.e(TAG, "Error starting jetty", e);
                
            }
        }
        
        public void sendMessage(int state)
        {
            Message msg = _handler.obtainMessage();
            Bundle b = new Bundle();
            b.putInt("state", state);
            msg.setData(b);
            _handler.sendMessage(msg);
        }
    }
    
    
    /**
     * JettyStopperThread
     *
     *
     */
    public class JettyStopperThread extends Thread
    { 
        android.os.Handler _handler;
        
        public JettyStopperThread(android.os.Handler handler)
        {
            _handler = handler;
        }
        
        public void run ()
        {
            try
            {
                sendMessage(__STOPPING);
                stopJetty();
                Log.i(TAG, "Jetty stopped");
                sendMessage(__STOPPED);
               
            }
            catch (Exception e)
            {
                
                sendMessage(__NOT_STOPPED);
                Log.e(TAG, "Error stopping jetty", e);
            }
        }
        
        public void sendMessage(int state)
        {
            Message msg = _handler.obtainMessage();
            Bundle b = new Bundle();
            b.putInt("state", state);
            msg.setData(b);
            _handler.sendMessage(msg);
        }
    }
    

    /**
     * Hack to get around bug in ResourceBundles
     * 
     * @param id
     * @return
     */
    public static InputStream getStreamToRawResource(int id)
    {
        if (__resources != null)
            return __resources.openRawResource(id);
        else
            return null;
    }


    
    public static boolean isRunning ()
    {
        return __isRunning;
    }
    
    /**
     * 
     */
    public IJettyService()
    {
        super();
        _handler = new android.os.Handler ()
        {
            public void handleMessage(Message msg) {
                switch (msg.getData().getInt("state"))
                {
                    case __STARTED:
                    {
                        IJettyToast.showServiceToast(IJettyService.this,R.string.jetty_started);
//                        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                        // The PendingIntent to launch IJetty activity if the user selects this notification
//                        PendingIntent contentIntent = PendingIntent.getActivity(IJettyService.this, 0,
//                                new Intent(IJettyService.this, IJetty.class), 0);
//
//                        CharSequence text = getText(R.string.manage_jetty);
//
//                        Notification notification = new Notification(R.drawable.ijetty_stat, 
//                                text, 
//                                System.currentTimeMillis());
//
//                        notification.setLatestEventInfo(IJettyService.this, getText(R.string.app_name),
//                                text, contentIntent);
//
//                        mNM.notify(R.string.jetty_started, notification);
                        
//                        Intent startIntent = new Intent(IJetty.__START_ACTION);
//                        startIntent.addCategory("default");
//                        Connector[] connectors = server.getConnectors();
//                        if (connectors != null)
//                        {
//                            String[] tmp = new String[connectors.length];
//                            
//                            for (int i=0;i<connectors.length;i++)
//                                tmp[i] = connectors[i].toString();
//
//                            startIntent.putExtra("connectors", tmp);
//                        }
//                       
//                        sendBroadcast(startIntent);
                        break;
                    }
                    case __NOT_STARTED:
                    {
                        IJettyToast.showServiceToast(IJettyService.this,R.string.jetty_not_started);
                        break;
                    }
                    case __STOPPED:
                    {
                        // Cancel the persistent notification.
                        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        mNM.cancel(R.string.jetty_started);
                        // Tell the user we stopped.
                        IJettyToast.showServiceToast(IJettyService.this,R.string.jetty_stopped);
//                        Intent stopIntent = new Intent(IJetty.__STOP_ACTION);
//                        stopIntent.addCategory("default");
//                        sendBroadcast(stopIntent);
                        break;
                    }
                    
                    case __NOT_STOPPED:
                    {
                        IJettyToast.showServiceToast(IJettyService.this,R.string.jetty_not_stopped);
                        break;
                    }
                    case __STARTING:
                    {
                        IJettyToast.showServiceToast(IJettyService.this,R.string.jetty_starting);
                        break;
                    }                    
                    case __STOPPING:
                    {
                        IJettyToast.showServiceToast(IJettyService.this,R.string.jetty_stopping);
                        break;
                    }
                }
               
            }
 
        };
    }
    
    


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /** 
     * Android Service create
     * @see android.app.Service#onCreate()
     */
    public void onCreate()
    {
        __resources = getResources();
        //getAssets().openFd("jetty").
//        try
//        {
//           Runtime.getRuntime().exec("sudo iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-ports 8080");
//            //Runtime.getRuntime().exec("iptables -t nat -A OUTPUT -p tcp -d 127.0.0.1 --dport 80 -j REDIRECT --to-ports 8080");
//        }
//        catch (IOException e1)
//        {
//            // TODO Auto-generated catch block
//            Log.d(TAG,"error");
//            e1.printStackTrace();
//        }
//        __JETTY_DIR = new File("/data/jetty");
        unZipAssets();
        __JETTY_DIR = new File("/data/data/com.joyplus.configuration/joyplus/wificonfig");
//        if(!__JETTY_DIR.exists()){
//            
//            __JETTY_DIR = new File("/data/data/com.joyplus.configuration/joyplus/wificonfig");
//        }
//        if(__JETTY_DIR.exists()){
//            Log.d(TAG,"asset   Ok---------------");
//        }else{
//            Log.d(TAG,"sdcard   Ok---------------");
//            __JETTY_DIR = new File(Environment.getExternalStorageDirectory(),"jetty");
//        }
        try
        {
            pi = getPackageManager().getPackageInfo(getPackageName(), 0); 
        }
        catch (Exception e)
        {
            Log.e(TAG, "Unable to determine running jetty version");
        }
    }
    
    
    private void unZipAssets(){
        InputStream dataSource;
        try
        {
            dataSource = getAssets().open("joyplus.zip");
            Log.d(TAG, "start unzip ---------------------->");
            unzip(dataSource, "/data/data/com.joyplus.configuration");
            Log.d(TAG, "end unzip ---------------------->");
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static void unzip(InputStream zipFileName, String outputDirectory) {
        try {
            ZipInputStream in = new ZipInputStream(zipFileName);
            // 获取ZipInputStream中的ZipEntry条目，一个zip文件中可能包含多个ZipEntry，
            // 当getNextEntry方法的返回值为null，则代表ZipInputStream中没有下一个ZipEntry，
            // 输入流读取完成；
            ZipEntry entry = in.getNextEntry();
            while (entry != null) {

                // 创建以zip包文件名为目录名的根目录
                File file = new File(outputDirectory);
                file.mkdir();
                if (entry.isDirectory()) {
                    String name = entry.getName();
                    name = name.substring(0, name.length() - 1);
              
                    file = new File(outputDirectory + File.separator + name);
                    file.mkdir();
               
                } else {
                    file = new File(outputDirectory + File.separator + entry.getName());
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);
                    byte buffer[] = new byte[1024*4];
                    int realLength;
                    while ((realLength = in.read(buffer)) > 0) {
                        out.write(buffer, 0, realLength);
                    }
                    out.close();
                }
                // 读取下一个ZipEntry
                entry = in.getNextEntry();
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO 自动生成 catch 块
            e.printStackTrace();
        }
    }


    /** 
     * Android Service Start
     * @see android.app.Service#onStart(android.content.Intent, int)
     */
    public void onStart(Intent intent, int startId)
    {
//        Runtime runtime = Runtime.getRuntime();
//        try
//        {
//            Process proc = runtime.exec("su");
//        }
//        catch (IOException e1)
//        {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
        if (server != null)
        {
            IJettyToast.showServiceToast(IJettyService.this,R.string.jetty_already_started);
            return;
        }

        try
        {
            preferences = PreferenceManager.getDefaultSharedPreferences(this);

//            String portDefault = getText(R.string.pref_port_value).toString();
            String sslPortDefault = getText(R.string.pref_ssl_port_value).toString();
            String pwdDefault = getText(R.string.pref_console_pwd_value).toString();
            
            String nioEnabledDefault = getText(R.string.pref_nio_value).toString();
            String sslEnabledDefault = getText(R.string.pref_ssl_value).toString();

            String portKey = getText(R.string.pref_port_key).toString();
            String sslPortKey = getText(R.string.pref_ssl_port_key).toString();
            String pwdKey = getText(R.string.pref_console_pwd_key).toString();
            String nioKey = getText(R.string.pref_nio_key).toString();
            String sslKey = getText(R.string.pref_ssl_key).toString();
            
            _useSSL = preferences.getBoolean(sslKey, Boolean.valueOf(sslEnabledDefault));
            _useNIO = preferences.getBoolean(nioKey, Boolean.valueOf(nioEnabledDefault));
//            _port = Integer.parseInt(preferences.getString(portKey, portDefault));
            _port = Integer.parseInt(ServerReciver.__PORT_DEFAULT);
            if (_useSSL)
            {
              _sslPort = Integer.parseInt(preferences.getString(sslPortKey, sslPortDefault));
              String defaultValue = getText(R.string.pref_keystore_pwd_value).toString();
              String key = getText(R.string.pref_keystore_pwd_key).toString();
              _keystorePassword = preferences.getString(key, defaultValue);
              
              defaultValue = getText(R.string.pref_keymgr_pwd_value).toString();
              key = getText(R.string.pref_keymgr_pwd_key).toString();
              _keymgrPassword = preferences.getString(key, defaultValue);
              
              defaultValue = getText(R.string.pref_truststore_pwd_value).toString();
              key = getText(R.string.pref_truststore_pwd_key).toString();
              _truststorePassword = preferences.getString(key, defaultValue);
              
              defaultValue = getText(R.string.pref_keystore_file).toString();
              key = getText(R.string.pref_keystore_file_key).toString();
              _keystoreFile = preferences.getString(key, defaultValue);
              
              defaultValue = getText(R.string.pref_truststore_file).toString();
              key = getText(R.string.pref_truststore_file_key).toString();
              _truststoreFile = preferences.getString(key, defaultValue);
            }

            _consolePassword = preferences.getString(pwdKey, pwdDefault);

            Log.d("Jetty", "pref port = "+_port);
            Log.d("Jetty", "pref use nio = "+_useNIO);
            Log.d("Jetty", "pref use ssl = "+_useSSL);
            Log.d("Jetty", "pref ssl port = "+_sslPort);
           
            //Get a wake lock to stop the cpu going to sleep
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "IJetty");
            wakeLock.acquire();

            new JettyStarterThread(_handler).start();
 
            super.onStart(intent, START_STICKY);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error starting jetty", e);
            IJettyToast.showServiceToast(IJettyService.this,R.string.jetty_not_started);
        }
    }


    /** 
     * Android Service destroy
     * @see android.app.Service#onDestroy()
     */
    public void onDestroy()
    {
        try
        {
            if (wakeLock != null)
            {
                wakeLock.release();
                wakeLock = null;
            }
            
            if (server != null)
            {
                new JettyStopperThread(_handler).start();
                
            }
            else
            {
                Log.i(TAG, "Jetty not running");
                IJettyToast.showServiceToast(IJettyService.this,R.string.jetty_not_running);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error stopping jetty", e);
            IJettyToast.showServiceToast(IJettyService.this,R.string.jetty_not_stopped);
        }
    }
    
    
   
    

    public void onLowMemory()
    {
        Log.i(TAG, "Low on memory");
        super.onLowMemory();
    }


    
    /**
     * Get a reference to the Jetty Server instance
     * @return
     */
    public Server getServer()
    {
        return server;
    }
    

    
    protected Server newServer()
    {
        return new Server();
    }
    
    protected ContextHandlerCollection newContexts()
    {
        return new ContextHandlerCollection();
    }
  
    
    protected void configureConnectors()
    {
        if (server != null)
        {
            if (_useNIO)
            {
                SelectChannelConnector nioConnector = new SelectChannelConnector();
                nioConnector.setUseDirectBuffers(false);
                nioConnector.setPort(_port);
                server.addConnector(nioConnector);
                Log.i(TAG, "Configured "+SelectChannelConnector.class.getName()+" on port "+_port);
            }
            else
            {
                SocketConnector bioConnector = new SocketConnector();
                bioConnector.setPort(_port);
                server.addConnector(bioConnector);
                Log.i(TAG, "Configured "+SocketConnector.class.getName()+" on port "+_port);
            }

            if (_useSSL)
            {
                SslContextFactory sslContextFactory = new SslContextFactory();
                sslContextFactory.setKeyStore(_keystoreFile);
                sslContextFactory.setTrustStore(_truststoreFile);
                sslContextFactory.setKeyStorePassword(_keystorePassword);
                sslContextFactory.setKeyManagerPassword(_keymgrPassword);
                sslContextFactory.setKeyStoreType("bks");
                sslContextFactory.setTrustStorePassword(_truststorePassword);
                sslContextFactory.setTrustStoreType("bks");

                //TODO SslSelectChannelConnector does not work on android 1.6, but does work on android 2.2
                if (_useNIO)
                {
                    SslSelectChannelConnector sslConnector = new SslSelectChannelConnector(sslContextFactory);
                    sslConnector.setPort(_sslPort);
                    server.addConnector(sslConnector);
                    Log.i(TAG, "Configured "+sslConnector.getClass().getName()+" on port "+_sslPort); 
                }
                else
                {
                    SslSocketConnector sslConnector = new SslSocketConnector(sslContextFactory);
                    sslConnector.setPort(_sslPort);
                    server.addConnector(sslConnector);
                    Log.i(TAG, "Configured "+sslConnector.getClass().getName()+" on port "+_sslPort); 
                }
               
            }
        }
    }
    
    protected void configureHandlers()
    {
        if (server != null)
        {
            HandlerCollection handlers = new HandlerCollection();
            contexts = new ContextHandlerCollection();
            handlers.setHandlers(new Handler[] {contexts, new DefaultHandler()});
            server.setHandler(handlers);  
        }
    }
    
    protected void configureDeployers () throws Exception
    {
        AndroidWebAppDeployer staticDeployer =  new AndroidWebAppDeployer();
        AndroidContextDeployer contextDeployer = new AndroidContextDeployer();
     
//        File jettyDir = IJetty.__JETTY_DIR;
        File jettyDir = __JETTY_DIR;
        Log.d(TAG,jettyDir.getAbsolutePath());
        if (jettyDir.exists())
        {
            // If the webapps dir exists, start the static webapp deployer
            if (new File(jettyDir, __WEBAPP_DIR).exists())
            {
                staticDeployer.setWebAppDir(__JETTY_DIR+"/"+__WEBAPP_DIR);
                staticDeployer.setDefaultsDescriptor(__JETTY_DIR+"/"+__ETC_DIR+"/webdefault.xml");
                staticDeployer.setContexts(contexts);
                staticDeployer.setAttribute(CONTENT_RESOLVER_ATTRIBUTE, getContentResolver());
                staticDeployer.setAttribute(ANDROID_CONTEXT_ATTRIBUTE, (Context) IJettyService.this);
                staticDeployer.setConfigurationClasses(__configurationClasses);
                staticDeployer.setAllowDuplicates(false);
            }          
           
            // Use a ContextDeploy so we can hot-deploy webapps and config at startup.
            if (new File(jettyDir, __CONTEXTS_DIR).exists())
            {
                contextDeployer.setScanInterval(10); // Don't eat the battery
                contextDeployer.setConfigurationDir(__JETTY_DIR+"/"+__CONTEXTS_DIR);                
                contextDeployer.setAttribute(CONTENT_RESOLVER_ATTRIBUTE, getContentResolver());
                contextDeployer.setAttribute(ANDROID_CONTEXT_ATTRIBUTE, (Context) IJettyService.this);             
                contextDeployer.setContexts(contexts);
            }
            
            if (server != null)
            {
                Log.i(TAG, "Adding context deployer: ");
                server.addBean(contextDeployer);
                Log.i(TAG, "Adding webapp deployer: ");
                server.addBean(staticDeployer); 
            }
        }
        else
        {
            Log.w(TAG, "Not loading any webapps - none on SD card.");
        }
    }
    
    public void configureRealm () throws IOException
    {
        File realmProps = new File(__JETTY_DIR+"/"+__ETC_DIR+"/realm.properties");
        if (realmProps.exists())
        {
            HashLoginService realm = new HashLoginService("Console", __JETTY_DIR+"/"+__ETC_DIR+"/realm.properties");
            realm.setRefreshInterval(0);
            if (_consolePassword != null)
                realm.putUser("admin", Credential.getCredential(_consolePassword), new String[]{"admin"}); //set the admin password for console webapp
            server.addBean(realm);
        }
    }
    
    
    protected void startJetty() throws Exception
    {

        //Set jetty.home
        System.setProperty ("jetty.home", __JETTY_DIR.getAbsolutePath());

        //ipv6 workaround for froyo
        System.setProperty("java.net.preferIPv6Addresses", "false");
        
        server = newServer();
        
        configureConnectors();
        configureHandlers();
        configureDeployers();
        configureRealm ();
    
        server.start();
        
        __isRunning = true;
        
        //TODO
        // Less than ideal solution to the problem that dalvik doesn't know about manifests of jars.
        // A as the version field is private to Server, its difficult
        //if not impossible to set it any other way. Note this means that ContextHandler.SContext.getServerInfo()
        //will still return 0.0.
        HttpGenerator.setServerVersion("i-jetty "+pi.versionName);
    }

    protected void stopJetty() throws Exception
    {
        try
        {
            Log.i(TAG, "Jetty stopping");
            server.stop();
            Log.i(TAG, "Jetty server stopped");
            server = null;
            __resources = null;
            __isRunning = false;
        }
        finally
        {
            Log.i(TAG,"Finally stopped");
        }
    }
    
    
}
