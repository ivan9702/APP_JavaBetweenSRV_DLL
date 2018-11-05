/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.net.URL;
import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.TrustManager;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.net.SocketTimeoutException;

import java.net.MalformedURLException;
import java.io.*;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

import com.startek_eng.fm220sdk.FM220SDKWrapper;
import java.io.BufferedReader;

//use GSON as json parser
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonParseException;

import java.util.Scanner;
import java.nio.charset.StandardCharsets;
/**
 *
 * @author lucky
 */

public class APP_JavaBetweenSRV_DLL {
    
    public static int UI_FP_Index = 1;
    public static String UI_User_ID = "035X";
    public static Boolean UI_HTTPS_Enable = true;
    public static int UI_Privilege = 1;
    
    public static String UI_Srv_IP = "192.168.1.76";
    public static String UI_Srv_Port = "8444";  //8444 for https, 
    
    public static long m_hConnect, m_hFPCapture, m_hFPImage, m_hEnrlSet;
    public static byte[] EncryptedMinutiae = new byte[512];
    public static byte[] EncryptedSessionKey = new byte[256];
    public static byte[] EncryptedDeleteData = new byte[256];
    public static int[] pEncryptedLen = new int[1];
    public static byte[] piv = new byte[16];

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)  {
        // TODO code application logic here
        Scanner scanner = new Scanner(System.in);
        String input;
        
        FM220SDKWrapper api = null;
	long hConnect = FM220SDKWrapper.FAIL;
	int rtn;
	try
	{
            api = FM220SDKWrapper.getInstance();
            if (api == null)
            {
                System.out.println("ERROR!! Failed on getting Instance of FC320SDKWrapper");
            }
        }
	catch (Exception e)
        {
            e.printStackTrace();
            return;
	}
        
        try 
        {
            Menu_ShowWelcome();
            Menu_ShowServerSettings();
            Menu_ShowUserSettings();

            while (true) 
            {
                Menu_ShowUserAllowItem_1();
                input = scanner.nextLine();
                if(input.equals("s"))
                {
                    ModifyServerSettings();
                    Menu_ShowServerSettings();
                }
                else if(input.equals("u"))
                {
                    ModifyUserSettings();
                    Menu_ShowUserSettings();    
                }
                else if(input.equals("1"))
                {
                    do_Enroll(api);
                }
                else if(input.equals("2"))
                {
                    do_Verify(api);
                }
                else if(input.equals("3"))
                {
                    do_Identify(api);
                }
                else if(input.equals("4"))
                {
                    do_Delete(api);
                }
                else if(input.equals("q"))
                {
                    break;
                }
                else
                {
                    System.out.println("Unknown input of :"+ input);
                }
            }
        } 
        catch(IllegalStateException e) 
        {
            // System.in has been closed
            System.out.println("System.in was closed; exiting");
        }
    }
    
    public static void Menu_ShowWelcome()
    {
        System.out.println("\n");
        System.out.println("---Welcome to STARTEK java to WebAPI Demo Program ---");
        System.out.println("---Please select what you want to do");
        System.out.println("\n");
    }
    
    public static void Menu_ShowUserAllowItem_1()
    {
        System.out.println("TYPE");
        System.out.println("\t \"s\" for modify SERVER settings");
        System.out.println("\t \"u\" for modify USER settings");
        System.out.println("\t \"1\" for finger Enroll");
        System.out.println("\t \"2\" for finger Verify");
        System.out.println("\t \"3\" for finger Identify");
        System.out.println("\t \"4\" for finger data Delete");
        System.out.println("\t \"q\" for quit program");
    }
    
    public static void Menu_ShowUserSettings()
    {
        System.out.println("Current User Settins as follow:");
        System.out.println("\t User ID (string):"+ UI_User_ID);
        System.out.println("\t Finger Index (range 1~10,eq: RIGHT thumb=1...RIGHT little=5, LEFT thumb=6...LEFT little=10):"+  Integer.toString(UI_FP_Index) );
        //System.out.println("\t Privilege (Range 0~2, means user level):"+  Integer.toString(UI_Privilege) );
        System.out.println("\n");
    }
    
    public static void ModifyUserSettings()
    {
        String input;
        Scanner scanner = new Scanner(System.in);
        System.out.print("Set User ID (string):");
        input = scanner.nextLine();
        UI_User_ID = input;
        
        System.out.print("Set Finger Index (range 1~10,eq: RIGHT thumb=1...RIGHT little=5, LEFT thumb=6...LEFT little=10):" );
        input = scanner.nextLine();
        UI_FP_Index = Integer.parseInt(input);
        
        //Allow UI_Privilege =1 only for demo purpose , 2018/10/22
        /*
        System.out.print("Set Privilege (Range 0~2, means user level):");
        input = scanner.nextLine();
        UI_Privilege = Integer.parseInt(input);
        */
    }
    
    public static void Menu_ShowServerSettings()
    {
        String UI_HTTPS_Enable_str = "";
        if(UI_HTTPS_Enable ==true)
        {
            UI_HTTPS_Enable_str="yes";
        }
        else
        {
            UI_HTTPS_Enable_str="no";
        }
        System.out.println("Current Redirect Server Settins as follow:");
        System.out.println("\t IP:"+ UI_Srv_IP);
        System.out.println("\t PORT:"+UI_Srv_Port);
        System.out.println("\t Enable HTTPS:"+ UI_HTTPS_Enable_str);
        System.out.println("\n");
    }
    
    public static void ModifyServerSettings()
    {
        String input;
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Set IP:");
        input = scanner.nextLine();
        UI_Srv_IP = input;
        
        System.out.print("Set PORT:");
        input = scanner.nextLine();
        UI_Srv_Port = input;
        
        System.out.print("Set Enable HTTPS (y/n):");
        input = scanner.nextLine();

        if(input.equals("y"))
        {
            UI_HTTPS_Enable =true;
        }
        else
        {
            UI_HTTPS_Enable =false;
        }

    }
    
    private static void do_Load_FP_Service()
    {
        //WebAPI_load_fp_srv();
    }
        
    private static void do_Enroll(FM220SDKWrapper api)
    {
        
        DeviceConnect(api);
        DLL_Enroll(api, true);
        DeviceDisconnect(api);
        
        String str_EncryptedMinutiae = ByteToHexString(EncryptedMinutiae);
        String str_EncryptedSessionKey = ByteToHexString(EncryptedSessionKey);
        String str_piv = ByteToHexString(piv);
        
        String json_str = BuildJson_Enroll(str_EncryptedMinutiae, str_EncryptedSessionKey, str_piv, UI_User_ID, UI_FP_Index, UI_Privilege);
        
        String results = Srv_Enroll(json_str);
        System.out.println("Srv Return: " + results);
    }
    
    private static void do_Identify(FM220SDKWrapper api)
    {
                
        DeviceConnect(api);
        DLL_Identify(api, true);
        DeviceDisconnect(api);
        
        String str_EncryptedMinutiae = ByteToHexString(EncryptedMinutiae);
        String str_EncryptedSessionKey = ByteToHexString(EncryptedSessionKey);
        String str_piv = ByteToHexString(piv);

        String json_str = BuildJson_Identify(str_EncryptedMinutiae, str_EncryptedSessionKey, str_piv);
        String results = Srv_Identify(json_str);
        System.out.println("Srv Return: " + results);

    }
    
    private static void do_Verify(FM220SDKWrapper api)
    {
        DeviceConnect(api);
        DLL_Verify(api, true);
        DeviceDisconnect(api);
        

        
        String str_EncryptedMinutiae = ByteToHexString(EncryptedMinutiae);
        String str_EncryptedSessionKey = ByteToHexString(EncryptedSessionKey);
        String str_piv = ByteToHexString(piv);
        
        String json_str = BuildJson_Verify(str_EncryptedMinutiae, str_EncryptedSessionKey, str_piv, UI_User_ID, UI_FP_Index, UI_Privilege);
        
        String results = Srv_Verify(json_str);
        System.out.println("Srv Return: " + results);
    }
    
    private static void do_Delete(FM220SDKWrapper api)
    {
        DeviceConnect(api);
        DLL_Delete(api, true, UI_User_ID, UI_FP_Index);
        DeviceDisconnect(api);

        String str_EncryptedDeleteData = ByteToHexString(EncryptedDeleteData);
        
        String json_str = BuildJson_Delete(UI_User_ID, str_EncryptedDeleteData);
        String results = Srv_Delete(json_str);
        System.out.println("Srv Return: " + results);

    }
    
    private static void do_Set_Session_key(FM220SDKWrapper api)
    {

    }
    
    private static String Srv_Enroll(String json_string)
    {
        Boolean https_en = UI_HTTPS_Enable;
        String ip = UI_Srv_IP;
        String port = UI_Srv_Port;
        String route = "/redirect/enroll";
        Boolean ignore_https_ca = true;
                    
        String ret_str = PostJson2RedirectServer(https_en,ip, port, route, json_string, ignore_https_ca);

        return ret_str;
    }

    private static String Srv_Verify(String json_string)
    {
        Boolean https_en = UI_HTTPS_Enable;
        String ip = UI_Srv_IP;
        String port = UI_Srv_Port;
        String route = "/redirect/verify";
        Boolean ignore_https_ca = true;

        String ret_str = PostJson2RedirectServer(https_en,ip, port, route, json_string, ignore_https_ca);

        return ret_str;
    }

    private static String Srv_Identify(String json_string)
    {
        Boolean https_en = UI_HTTPS_Enable;
        String ip = UI_Srv_IP;
        String port = UI_Srv_Port;
        String route = "/redirect/identify";
        Boolean ignore_https_ca = true;

        String ret_str = PostJson2RedirectServer(https_en,ip, port, route, json_string, ignore_https_ca);

        return ret_str;
    }

    private static String Srv_Delete(String json_string)
    {
        Boolean https_en = UI_HTTPS_Enable;
        String ip = UI_Srv_IP;
        String port = UI_Srv_Port;
        String route = "/redirect/delete";
        Boolean ignore_https_ca = true;

        String ret_str = PostJson2RedirectServer(https_en,ip, port, route, json_string, ignore_https_ca);

        return ret_str;
    }
        
    private static String PostJson2RedirectServer(boolean https_en,String SrvIp, String port, String route, String json_string, Boolean Ignore_CA)
    {
        String protocol = "";
        String ret_str = "";
        
        if(https_en == true)
        {
            protocol = "https://" + SrvIp + ":" + port;
        }
        else
        {
            protocol = "http://"+ SrvIp + ":" + port;
        }
        
        //HttpURLConnection connection = null; 
         DataOutputStream wr;
         InputStream is;
        try
        {
            URL url = new URL(protocol + route);
            if(https_en == true)
            {
                if(Ignore_CA == true)   //if need to ignore CA (ex. self signed CA for HTTPS)
                {
                    TrustManager[] trustAllCerts = new TrustManager[] { 
                        new X509TrustManager() {
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
                                return new X509Certificate[0];
                            } 
                            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                            } 
                            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                            }
                        } 
                    }; 
                
                    SSLContext sc = SSLContext.getInstance("SSL"); 
                    sc.init(null, trustAllCerts, new java.security.SecureRandom()); 
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                    
                    HostnameVerifier allHostsValid = new HostnameVerifier() 
                    {
                        public boolean verify(String hostname, SSLSession session) 
                        {
                            return true;
                        };
                    };

                    // Install the all-trusting host verifier
                    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
                    
                }

                
                HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type","application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Length", Integer.toString(json_string.length())); //?
                connection.setRequestProperty("User-agent","myapp");
                connection.setConnectTimeout(120000);
                connection.setReadTimeout(120000);
                connection.setUseCaches(false); 
                connection.setDoOutput(true);
                connection.setDoInput(true);
            
                //Write out
                wr = new DataOutputStream (connection.getOutputStream ());
                wr.writeBytes (json_string);
                wr.flush ();
                wr.close ();
                is = connection.getInputStream();
            }
            else
            {
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type","application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Length", Integer.toString(json_string.length())); //?
                connection.setRequestProperty("User-agent","myapp");
                connection.setConnectTimeout(120000);
                connection.setReadTimeout(120000);
                connection.setUseCaches(false); 
                connection.setDoOutput(true);
                connection.setDoInput(true);
                //Write out
                wr = new DataOutputStream (connection.getOutputStream ());
                wr.writeBytes (json_string);
                wr.flush ();
                wr.close ();
                is = connection.getInputStream();
            }
            /*
            wr.writeBytes (json_string);
            wr.flush ();
            wr.close ();

            */
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer(); 
            while((line = rd.readLine()) != null) 
            {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            ret_str = response.toString();
            //return response.toString();
            
            
        }
        catch(SocketTimeoutException e)
        {
            System.out.println("TIMEOUT: " + e);
        }
        catch(Exception e) 
        {
            System.out.println("ERROR: " + e);
        }
        finally 
        {
            //if(connection != null) 
            {
                //connection.disconnect(); 
                
            }
        }    
        return ret_str;
    }
    
    private static int Snap_old(FM220SDKWrapper s,long hConnect)
    {
        if (s==null)
        {
            System.out.println("ERROR!! Fm220SDKWrapper is not initialed");
            return FM220SDKWrapper.FAIL;
        }
        if (hConnect<0)
        {
            System.out.println("ERROR!! Device not connected");
            return FM220SDKWrapper.FAIL;
        }
	long hFPImage = 0;
        long hFPCapture=0;
	int rtn,result =FM220SDKWrapper.FAIL;

        System.out.println("\nPlease put your finger on the reader...");
	try
	{
            if( (hFPCapture=s.FP_CreateCaptureHandle(hConnect))<0)
            {
		System.out.println("ERROR!! FP_CreateSnapHandle() failed!!");
		return FM220SDKWrapper.FAIL;
            }
            if( (hFPImage=s.FP_CreateImageHandle(hConnect,(byte)FM220SDKWrapper.GRAY_IMAGE,FM220SDKWrapper.LARGE))<0)
            {
		System.out.println("ERROR!! FP_CreateImageHandle() failed!!");
		s.FP_DestroyCaptureHandle(hConnect,hFPCapture);
		return FM220SDKWrapper.FAIL;
            }
            
            //Please Remove Finger
            while(true)
            {   //check fingerprint is removed
		rtn=s.FP_CheckBlank(hConnect);
		if(rtn!=FM220SDKWrapper.FAIL)
                	break;
		
                System.out.println("Please Remove your finger!!!");
            }//end while(true)
            
            
            while((result=s.FP_Capture(hConnect,hFPCapture))!=FM220SDKWrapper.OK)//Capture fingerprint 
            {   //Show Image Status
                switch(result & FM220SDKWrapper.U_POSITION_CHECK_MASK)
                {
                    case FM220SDKWrapper.U_POSITION_TOO_LOW:
                        System.out.println("Put your finger higher");
                        break;
                    case FM220SDKWrapper.U_POSITION_TOO_TOP:
                        System.out.println("Put your finger lower");
                        break;
                    case FM220SDKWrapper.U_POSITION_TOO_RIGHT:
                        System.out.println("Put your finger further to the left");
                        break;
                    case FM220SDKWrapper.U_POSITION_TOO_LEFT:
                        System.out.println("Put your finger further to the right");
                        break;
                    case FM220SDKWrapper.U_POSITION_TOO_LOW_RIGHT:
                        System.out.println("Put your finger further to the upper left");
                        break;
                    case FM220SDKWrapper.U_POSITION_TOO_LOW_LEFT:
                        System.out.println("Put your finger further to the upper right");
                        break;
                    case FM220SDKWrapper.U_POSITION_TOO_TOP_RIGHT:
                        System.out.println("Put your finger further to the lower left");
                        break;
                    case FM220SDKWrapper.U_POSITION_TOO_TOP_LEFT:
                        System.out.println("Put your finger further to the lower right");
                        break;
                    case FM220SDKWrapper.U_POSITION_OK:
                        System.out.println("Position is OK");
                        break;
                    case FM220SDKWrapper.U_POSITION_NO_FP:
                    default:
                        System.out.println("Make a closer contact with the reader");
                        break;
                } //end switch
                
                switch(result & FM220SDKWrapper.U_DENSITY_CHECK_MASK)
                {
                    case FM220SDKWrapper.U_DENSITY_TOO_DARK:
                        System.out.println("Wipe off excess moisture or put lighter");
                        break;
                    case FM220SDKWrapper.U_DENSITY_TOO_LIGHT:
                        System.out.println("Moisten your finger or put heavier");
                        break;
                    case FM220SDKWrapper.U_DENSITY_AMBIGUOUS:
                    default:
                        System.out.println("Please examine your finger");
                        break;
                }
                
                if( (rtn=s.FP_GetImage(hConnect,hFPImage) )!=FM220SDKWrapper.OK)
                {
                    System.out.println("ERROR!! FP_GetImage() failed!!");
                    break;		
                }                  
            } //end wile
            if(result==FM220SDKWrapper.OK)
            {
                s.FP_SaveImage(hConnect,hFPImage,FM220SDKWrapper.BMP,"Snap.bmp");                
                //s.FP_SaveISOImage(hConnect,hFPImage,FM220SDKWrapper.ISO,"Snap.isi",(byte)0,(byte)0);
                System.out.println("FP_Capture() OK [Snap.bmp:"+result+"]");
            }
	}
	catch (Exception e)
	{
            e.printStackTrace();
	}
	finally
	{
            if (hFPCapture>FM220SDKWrapper.OK)
		s.FP_DestroyCaptureHandle(hConnect,hFPCapture);
            
            if (hFPImage>FM220SDKWrapper.OK)
                s.FP_DestroyImageHandle (hConnect, hFPImage);
	}
        return result;
    }
    

    
    private static int Enroll_SampleCode(FM220SDKWrapper s,long hConnect)
    {
        if (s==null)
        {
            System.out.println("ERROR!! FM220SDKWrapper is not initialed");
            return FM220SDKWrapper.FAIL;
        }
        if (hConnect<FM220SDKWrapper.OK)
        {
            System.out.println("ERROR!! Device not connected");
            return FM220SDKWrapper.FAIL;
        }
		long hFPEnroll = FM220SDKWrapper.FAIL;
        int rtn=FM220SDKWrapper.FAIL;
	int result = FM220SDKWrapper.FAIL;
        byte[] p_code = new byte[FM220SDKWrapper.FP_CODE_LENGTH];
	byte[] fp_code = new byte[FM220SDKWrapper.FP_CODE_LENGTH];
        
	try
	{
            if ( (hFPEnroll = s.FP_CreateEnrollHandle( hConnect,(byte)FM220SDKWrapper.DEFAULT_MODE) )<FM220SDKWrapper.OK) 
            {
                System.out.println("ERROR!! FP_CreateEnrollHandle() failed!");
                return  FM220SDKWrapper.FAIL;					         
            }
            for(int i=0;i<3;i++)
            {
                rtn = Snap_old(s,hConnect);
                if(rtn==FM220SDKWrapper.OK)
		{
                    System.out.println("FP_Capture() OK");
                    //rtn = s.FP_GetPrimaryCode(hConnect,p_code);
                    rtn = s.FP_GetTemplate(hConnect,p_code,1,0);	//1: ISO 19794-2 format
                    if(rtn==FM220SDKWrapper.OK)
                    {
			System.out.println("FP_GetTemplate() OK");
			//rtn  = s.FP_Enroll(hConnect,hFPEnroll, p_code, fp_code);
			rtn  = s.FP_EnrollEx(hConnect,hFPEnroll, p_code, fp_code,1);
			if(rtn==FM220SDKWrapper.U_CLASS_A || rtn==FM220SDKWrapper.U_CLASS_B)
			{   //save enrolled fingerprint template
                            saveBytes2File("Enroll.dat",fp_code);
                            System.out.println("Enroll OK [Enroll.dat]");
                            break;
			}
                    }
		}
            }  //end for
            
            if(rtn!=FM220SDKWrapper.U_CLASS_A && rtn!=FM220SDKWrapper.U_CLASS_B)
				System.out.println("Enroll fail!!");
	}  //end try
	catch (Exception e)
	{
            e.printStackTrace();
	}
	finally
	{
            if (hFPEnroll>=FM220SDKWrapper.OK)
		s.FP_DestroyEnrollHandle(hConnect,hFPEnroll);
	}
        return result;
    }
    
    private static long Match_SmapleCode(FM220SDKWrapper s,long hConnect)
    {
        if (s==null)
        {
            System.out.println("ERROR!! FC320SDKWrapper is not initialed");
            return FM220SDKWrapper.FAIL;
        }
        if (hConnect<FM220SDKWrapper.OK)
        {
            System.out.println("ERROR!! Device not connected");
            return FM220SDKWrapper.FAIL;
        }
        byte[] fp_code = new byte[FM220SDKWrapper.FP_CODE_LENGTH];
        byte[] p_code = new byte[FM220SDKWrapper.FP_CODE_LENGTH];
        long result= FM220SDKWrapper.FAIL;
		int rtn=FM220SDKWrapper.FAIL;
        long score = 0;
        
        //if enrolled file existed
        fp_code=readBytesFromFile("Enroll.dat");
        if (fp_code==null)
        {
            System.out.println("ERROR!! Fingerprint not enrolled [Read Enroll.dat failed]");
            return FM220SDKWrapper.FAIL;
        }
        try
        {
            rtn = Snap_old(s,hConnect);
            if(rtn==FM220SDKWrapper.OK)
            {
                System.out.println("FP_Capture() OK");
                //rtn = s.FP_GetPrimaryCode(hConnect,p_code);
                rtn = s.FP_GetTemplate(hConnect,p_code,1,0);
                if (rtn==FM220SDKWrapper.OK)
                {
                    score = s.FP_CodeMatchEx(hConnect,p_code,fp_code,FM220SDKWrapper.SECURITY_C);
                    System.out.println("Match score="+score);
                    result = score;
                }
                else
                {
                    System.out.println("ERROR!! FP_GetTemplate() failed!!");
                }
            }
        }
        catch (Exception e)
	{
            e.printStackTrace();
	}
	finally
	{
	}
        return result;       
    }
    
    private static byte[] readBytesFromFile(String filePath)
    {
        byte[] result = null;
        try
        {
            java.io.FileInputStream in = new java.io.FileInputStream(filePath);
            byte[] tmpCode = new byte[FM220SDKWrapper.FP_CODE_LENGTH];
            if (in.read(tmpCode)==FM220SDKWrapper.FP_CODE_LENGTH)
                result = tmpCode;
            in.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }
     
    
    private static int saveBytes2File(String filePath,byte[] data)
    {
        int result =FM220SDKWrapper.FAIL ;
        try
        {
            java.io.FileOutputStream out = new java.io.FileOutputStream(filePath);
            out.write(data);
            out.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }
    
    public static long DeviceConnect(FM220SDKWrapper api)
    {
        try
        {
            m_hConnect = api.FP_ConnectCaptureDriver(0);
        }
	catch (Exception e)
	{
            e.printStackTrace();
	}
        
        if (m_hConnect != 0)
            System.out.println("Connect FM220 succeess.");
        else
            System.out.println("Connect FM220 fail!");

        return m_hConnect;
    }

    public static void DeviceDisconnect(FM220SDKWrapper api)
    {
        api.FP_DisconnectCaptureDriver(m_hConnect);
        System.out.println("Disonnect FM220. ");
    }
    
    private static int Snap(FM220SDKWrapper api, boolean save_img )
    {
        int rtn;
        if (api == null)
        {
            System.out.println("ERROR!! Fm220SDKWrapper is not initialed");
            return FM220SDKWrapper.FAIL;
        }

        System.out.println("Snap start.");
        
        try
        {
            m_hFPCapture = api.FP_CreateCaptureHandle(m_hConnect);
            if(m_hFPCapture < 0)
            {
                System.out.println("ERROR!! FP_CreateSnapHandle() failed!!");
                return FM220SDKWrapper.FAIL;
            }
            
            m_hFPImage = api.FP_CreateImageHandle(m_hConnect, (byte)FM220SDKWrapper.GRAY_IMAGE,FM220SDKWrapper.LARGE);
            if(m_hFPImage < 0)
            {
                System.out.println("ERROR!! FP_CreateImageHandle() failed!!");
                api.FP_DestroyCaptureHandle(m_hConnect,m_hFPCapture);
                return FM220SDKWrapper.FAIL;
            }
            
            while (api.FP_Capture(m_hConnect, m_hFPCapture) != FM220SDKWrapper.OK)
            {
                rtn = api.FP_GetImage(m_hConnect, m_hFPImage);
                System.out.println("\nPlease put your finger on the reader...");
            }
            rtn = api.FP_GetImage(m_hConnect, m_hFPImage);
            System.out.println("\nFingerprint Capture Success !");
            
            if(save_img == true)
            {
                rtn = api.FP_SaveImage(m_hConnect, m_hFPImage, FM220SDKWrapper.BMP, "temp.bmp");
                System.out.println("\nFingerprint save as temp.bmp OK !");
            }
            
            rtn = api.FP_DestroyCaptureHandle(m_hConnect, m_hFPCapture);
            rtn = api.FP_DestroyImageHandle(m_hConnect, m_hFPImage);
            return FM220SDKWrapper.OK;
        }
        catch (Exception e)
	{
            e.printStackTrace();
            return FM220SDKWrapper.FAIL;
	}
    }
    
    public static int DLL_Enroll(FM220SDKWrapper api, boolean save_img )
    {
        int rtn = 0, Enrl_count;

        try
        {
            m_hEnrlSet = api.FP_CreateEnrollHandle(m_hConnect, (byte)FM220SDKWrapper.DEFAULT_MODE);
            if (m_hEnrlSet == 0)
            {
                System.out.println("\nCreate Enroll fail !");
                return FM220SDKWrapper.FAIL;
            }
            else
            {
                System.out.println("\nCreateEnroll Success. ");
            }
            
            for (Enrl_count = 0; Enrl_count < 3; Enrl_count++)
            {
                while ((rtn = api.FP_CheckBlank(m_hConnect)) != FM220SDKWrapper.OK)
                {
                    System.out.println("\nPut finger OFF reader");
                }

                Snap(api, save_img);

                rtn = api.FP_EnrollEx_Encrypted(m_hConnect, m_hEnrlSet, EncryptedMinutiae, 1, piv, EncryptedSessionKey); //EncryptedEnrolledMinutiae is from AES 256, EncryptedSessionKey is from RSA
                if (rtn == 65)  //class A
                {
                    System.out.println("\nDll get EncryptedMinutiae Success!");
                    api.FP_DestroyEnrollHandle(m_hConnect, m_hEnrlSet);

                    return FM220SDKWrapper.OK;
                }
            }
        }
        catch (Exception e)
	{
            e.printStackTrace();
            return FM220SDKWrapper.FAIL;
	}
        
        System.out.println("Dll get EncryptedMinutiae Success!");
        System.out.println("Enroll Fail!, return = " + rtn);

        api.FP_DestroyEnrollHandle(m_hConnect, m_hEnrlSet);

        return FM220SDKWrapper.FAIL;
    }
    
    public static int DLL_Verify(FM220SDKWrapper api, boolean save_img)
    {
        int rtn = 0;

        Snap(api, save_img);
        
        try
        {
            rtn = api.FP_GetEncryptedTemplate(m_hConnect, EncryptedMinutiae, 1, 0, piv, EncryptedSessionKey);
        }
        catch (Exception e)
	{
            e.printStackTrace();
            return FM220SDKWrapper.FAIL;
	}

        System.out.println("FP_GetEncryptedTemplate = " + rtn);

        return FM220SDKWrapper.OK;
    }

    public static int DLL_Identify(FM220SDKWrapper api, boolean save_img)
    {
        int rtn = 0;

        Snap(api, save_img);
        
        try
        {
            rtn = api.FP_GetEncryptedTemplate(m_hConnect, EncryptedMinutiae, 1, 0, piv, EncryptedSessionKey);
        }
        catch (Exception e)
	{
            e.printStackTrace();
            return FM220SDKWrapper.FAIL;
	}

        System.out.println("FP_GetEncryptedTemplate = " + rtn);

        return FM220SDKWrapper.OK;
    }

    public static int DLL_Delete(FM220SDKWrapper api, boolean save_img, String User_Id, int fp_idx)
    {

        int rtn = 0;
        byte[] UserId = new byte[256] ;
        //UserId = Encoding.ASCII.GetBytes(User_Id);
        UserId = User_Id.getBytes(StandardCharsets.US_ASCII);
        if (UserId.length > 256)
        {
            return -1;
        }
        
        try
        {
            rtn = api.FP_GetDeleteData(m_hConnect, UserId, fp_idx, EncryptedDeleteData, pEncryptedLen);
        }
        catch (Exception e)
	{
            e.printStackTrace();
            return FM220SDKWrapper.FAIL;
	}

        System.out.println("FP_GetDeleteData = " + rtn);

        return FM220SDKWrapper.OK;
    }
    
    public static String BuildJson_Enroll(String encMinutiae, String eSkey, String iv, String id, int fp_idx, int privilege)
    {
        //put together as new serialize json string as server need
        json_srv_enroll json_to_srv = new json_srv_enroll();

        //using (var ms = new MemoryStream())
        {

            //assign one json to another json 
            json_to_srv.encMinutiae = encMinutiae;
            json_to_srv.eSkey = eSkey;
            json_to_srv.iv = iv;
            json_to_srv.clientUserId = id; 
            json_to_srv.fpIndex = fp_idx;
            json_to_srv.privilege = privilege;
        }
        
        Gson gson = new Gson();
        String ret = gson.toJson(json_to_srv);
        
        return ret;
    }
    public static String BuildJson_Verify(String encMinutiae, String eSkey, String iv, String id, int fp_idx, int privilege)
    {
        return BuildJson_Enroll(encMinutiae, eSkey, iv, id, fp_idx, privilege);
    }

    public static String BuildJson_Identify(String encMinutiae, String eSkey, String iv)
    {
        //put together as new serialize json string as server need
        json_srv_identify json_to_srv = new json_srv_identify();
        //String ret_str;
        //using (var ms = new MemoryStream())
        {
            //assign one json to another json 
            json_to_srv.encMinutiae = encMinutiae;
            json_to_srv.eSkey = eSkey;
            json_to_srv.iv = iv;

        }
        Gson gson = new Gson();
        String ret = gson.toJson(json_to_srv);
        
        return ret;
    }

    public static String BuildJson_Delete(String clientUserId, String deleteData)
    {
        //put together as new serialize json string as server need
        json_srv_delete json_to_srv = new json_srv_delete();

        {
            //assign one json to another json 
            json_to_srv.clientUserId = clientUserId;
            json_to_srv.deleteData = deleteData;
        }
        Gson gson = new Gson();
        String ret = gson.toJson(json_to_srv);
        
        return ret;
    }
    
    private static String ReComposeJson_Enroll(String json_in)
    {
        String ret = "";
        Gson gson_in = new Gson();
        json_get_minutiae json_obj_in = gson_in.fromJson(json_in, json_get_minutiae.class);
            
        try
        {
            json_srv_enroll json_obj_out = new json_srv_enroll();
            
            json_obj_out.set_clientUserId(UI_User_ID);// = UI_User_ID;
            json_obj_out.set_fpIndex(UI_FP_Index);// = UI_FP_Index;
            json_obj_out.set_privilege(UI_Privilege);// = UI_Privilege;
            json_obj_out.set_encMinutiae(json_obj_in.data.get_encMinutiae());// = json_obj_in.data.get_encMinutiae();
            json_obj_out.set_eSkey(json_obj_in.data.get_eSkey());// = json_obj_in.data.get_eSkey();
            json_obj_out.set_iv(json_obj_in.data.get_iv());// = json_obj_in.data.get_iv();
               
            Gson gson_out = new Gson();
            ret = gson_out.toJson(json_obj_out); 
         }
        catch(Exception e)
        {
              System.out.println("ERROR in ReComposeJson_Enroll(): " + e);
        }
        return ret;
    }
    
    private static String ReComposeJson_Identify(String json_in)
    {
        String ret = "";
        Gson gson_in = new Gson();
        json_get_minutiae json_obj_in = gson_in.fromJson(json_in, json_get_minutiae.class);
            
        try
        {
            json_srv_identify json_obj_out = new json_srv_identify();

            json_obj_out.set_encMinutiae(json_obj_in.data.get_encMinutiae());// = json_obj_in.data.get_encMinutiae();
            json_obj_out.set_eSkey(json_obj_in.data.get_eSkey());// = json_obj_in.data.get_eSkey();
            json_obj_out.set_iv(json_obj_in.data.get_iv());// = json_obj_in.data.get_iv();
               
            Gson gson_out = new Gson();
            ret = gson_out.toJson(json_obj_out); 
         }
        catch(Exception e)
        {
              System.out.println("ERROR in ReComposeJson_Identify(): " + e);
        }
        return ret;
    }
        
    private static String ReComposeJson_Delete(String json_in)
    {
        String ret = "";
        Gson gson_in = new Gson();
        json_get_delete_data json_obj_in = gson_in.fromJson(json_in, json_get_delete_data.class);
            
        try
        {
            json_srv_delete json_obj_out = new json_srv_delete();

            json_obj_out.set_clientUserId(json_obj_in.data.get_clientUserId());// = json_obj_in.data.get_encMinutiae();
            json_obj_out.set_deleteData(json_obj_in.data.get_deleteData());// = json_obj_in.data.get_eSkey();
               
            Gson gson_out = new Gson();
            ret = gson_out.toJson(json_obj_out); 
         }
        catch(Exception e)
        {
              System.out.println("ERROR in ReComposeJson_Delete(): " + e);
        }
        return ret;
    }
    
    public static String ByteToHexString ( byte buf[] ) 
    {
             
        StringBuffer strbuf = new StringBuffer( buf.length * 2 );
        int i;
 
        for ( i = 0; i < buf.length; i++ ) 
        {
             
            if ( ( ( int ) buf[i] & 0xff ) < 0x10 )
                
            strbuf.append("0");
 
            strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
        }     
 
        return strbuf.toString();
    }
    
    public class json_data_get_minutiae {
        @SerializedName("encMinutiae")
        private String encMinutiae;
        public String get_encMinutiae() { return encMinutiae; }
        public void set_encMinutiae(String data) { this.encMinutiae = data;}

        @SerializedName("eSkey")
        private String eSkey;
        public String get_eSkey() { return eSkey; }
        public void set_eSkey(String data) { this.eSkey = data; }
        
        @SerializedName("iv")
        private String iv;
        public String get_iv() { return iv;}
        public void set_iv(String data) { this.iv = data; }
    }
    
    public class json_get_minutiae {
        @SerializedName("code")
        private String code;
        public String get_code() { return code;}
        public void set_code(String data) {this.code = data;}

        @SerializedName("message")
        private String message;
        public String get_message() { return message; }
        public void set_message(String data) { this.message = data; }
        
        @SerializedName("data")
        private json_data_get_minutiae data;
        public json_data_get_minutiae get_data() { return data; }
        public void set_data(json_data_get_minutiae data_in) { this.data = data_in; }
    }

    public static class json_set_session_key {
        @SerializedName("code")
        private String code;
        public String get_code() { return code;}
        public void set_code(String data) {this.code = data;}

        @SerializedName("message")
        private String message;
        public String get_message() { return message; }
        public void set_message(String data) { this.message = data; }
    }
    
    public static class json_data_get_delete_data {
        @SerializedName("clientUserId")
        private String clientUserId;
        public String get_clientUserId() { return clientUserId;}
        public void set_clientUserId(String data) {this.clientUserId = data;}

        @SerializedName("deleteData")
        private String deleteData;
        public String get_deleteData() { return deleteData; }
        public void set_deleteData(String data) { this.deleteData = data; }
    }
        
    public static class json_get_delete_data {
        @SerializedName("code")
        private String code;
        public String get_code() { return code;}
        public void set_code(String data) {this.code = data;}

        @SerializedName("message")
        private String message;
        public String get_message() { return message; }
        public void set_message(String data) { this.message = data; }
        
        @SerializedName("data")
        private json_data_get_delete_data data;
        public json_data_get_delete_data get_data() { return data; }
        public void set_data(json_data_get_delete_data data_in) { this.data = data_in; }
    }
    
    public static class json_srv_enroll {
        @SerializedName("encMinutiae")
        private String encMinutiae;
        public String get_encMinutiae() { return encMinutiae;}
        public void set_encMinutiae(String data) {this.encMinutiae = data;}

        @SerializedName("eSkey")
        private String eSkey;
        public String get_eSkey() { return eSkey; }
        public void set_eSkey(String data) { this.eSkey = data; }
        
        @SerializedName("iv")
        private String iv;
        public String get_iv() { return iv; }
        public void set_iv(String data) { this.iv = data; }
        
        @SerializedName("clientUserId")
        private String clientUserId;
        public String get_clientUserId() { return clientUserId; }
        public void set_clientUserId(String data) { this.clientUserId = data; }
        
        @SerializedName("fpIndex")
        private int fpIndex;
        public int get_fpIndex() { return fpIndex; }
        public void set_fpIndex(int data) { this.fpIndex = data; }
        
        @SerializedName("privilege")
        private int privilege;
        public int get_privilege() { return privilege; }
        public void set_privilege(int data) { this.privilege = data; }
    }
        
    public static class json_srv_identify {
        @SerializedName("encMinutiae")
        private String encMinutiae;
        public String get_encMinutiae() { return encMinutiae;}
        public void set_encMinutiae(String data) {this.encMinutiae = data;}

        @SerializedName("eSkey")
        private String eSkey;
        public String get_eSkey() { return eSkey; }
        public void set_eSkey(String data) { this.eSkey = data; }
        
        @SerializedName("iv")
        private String iv;
        public String get_iv() { return iv; }
        public void set_iv(String data_in) { this.iv = data_in; }
    }
        
        
    public static class json_srv_delete {
        @SerializedName("clientUserId")
        private String clientUserId;
        public String get_clientUserId() { return clientUserId;}
        public void set_clientUserId(String data) {this.clientUserId = data;}

        @SerializedName("deleteData")
        private String deleteData;
        public String get_deleteData() { return deleteData; }
        public void set_deleteData(String data) { this.deleteData = data; }
    }

}
