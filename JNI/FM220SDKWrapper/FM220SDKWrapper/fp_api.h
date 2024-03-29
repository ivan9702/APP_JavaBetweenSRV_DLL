/*******************************************************************\
*   FC100API.h  :  header file of FC100API.LIB                      *
\*******************************************************************/
#include   <windows.h>
#include   <windowsx.h>

typedef    HANDLE             HFPIMAGE;			    
typedef    HANDLE            HFPENROLL;			    
typedef    HANDLE            HFPCAPTURE;			    
typedef    HANDLE            HCONNECT;			    

#define	   FM_SERIES
#define    OK                           0
#define    FAIL                        -1
#define    CANCELLED                   -2
#define    GENERATE_ENCRYPT_DATA_FAIL  -3
#define    ENCRYPT_SESSION_KEY_FAIL    -4
#define    CONNECT_SERVER_FAIL         -5
#define    SERVER_RESPONSE_FAIL        -6

#define        SNAP_TIME_LIMIT      100
#define        SNAP_COUNT				 100

#define    LPT1                         0
#define    LPT2                         1

#define    PRN_300DPI                   0
#define    PRN_150DPI                   1
#define    PRN_100DPI                   2
#define    PRN_75DPI                    3
#define    PRN_600DPI                   4 

/*-------------------------------------------*\
|   Return code of system integrator level    |
\*-------------------------------------------*/
#define    S_DOMAIN_ERR                -1
#define    S_RANGE_ERR                 -2
#define    S_MEM_ERR                   -3
#define    S_FILE_ERR                  -4
#define    S_COMM_ERR                  -5
#define    S_CHKSUM_ERR                -6
#define    S_TIME_OUT                  -7
#define    S_PRINT_ERR                 -8

#define    S_KEYCARD_CHECK_FAIL       -10
#define    S_EMPTY_IMAGE_SET          -11

#define    S_FPSET_INVALID            -21
#define    S_FPCODE_INVALID           -22
#define    S_FP_INVALID               -23
#define    S_SECURITY_ERR             -24

#define    S_NOT_SUPPORTED             -1
#define    S_VALID                   1
#define    S_INVALID               255  

#define     IDD_SNAP_MSG1               412
#define     IDD_SNAP_MSG2               413

#define     IDD_SNAP_GROUP              421

#define     IDD_ENRL_RLT                431

#define     IDS_POSITION_NO_FP          501
#define     IDS_POSITION_TOO_LOW        502 
#define     IDS_POSITION_TOO_TOP        503 
#define     IDS_POSITION_TOO_RIGHT      504 
#define     IDS_POSITION_TOO_LEFT       505 
#define     IDS_POSITION_TOO_LOW_RIGHT  506 
#define     IDS_POSITION_TOO_LOW_LEFT   507 
#define     IDS_POSITION_TOO_TOP_RIGHT  508 
#define     IDS_POSITION_TOO_TOP_LEFT   509 
#define     IDS_POSITION_OK             510

#define     IDS_DENSITY_TOO_DARK        511 
#define     IDS_DENSITY_TOO_LIGHT       512 
#define     IDS_DENSITY_AMBIGUOUS       513 
#define     IDS_DENSITY_OK             514
/*------------------------------*\
|   Return code of user level    |
\*------------------------------*/
#define    U_LEFT                     -41
#define    U_RIGHT                    -42
#define    U_UP                       -43
#define    U_DOWN                     -44

#define    U_POSITION_CHECK_MASK      0x00002F00
#define    U_POSITION_NO_FP           0x00002000
#define    U_POSITION_TOO_LOW         0x00000100
#define    U_POSITION_TOO_TOP         0x00000200
#define    U_POSITION_TOO_RIGHT       0x00000400
#define    U_POSITION_TOO_LEFT        0x00000800
#define    U_POSITION_TOO_LOW_RIGHT   (U_POSITION_TOO_LOW|U_POSITION_TOO_RIGHT)
#define    U_POSITION_TOO_LOW_LEFT    (U_POSITION_TOO_LOW|U_POSITION_TOO_LEFT)
#define    U_POSITION_TOO_TOP_RIGHT   (U_POSITION_TOO_TOP|U_POSITION_TOO_RIGHT)
#define    U_POSITION_TOO_TOP_LEFT    (U_POSITION_TOO_TOP|U_POSITION_TOO_LEFT)
#define     U_POSITION_OK             0x00000000

#define    U_DENSITY_CHECK_MASK       0x000000E0
#define    U_DENSITY_TOO_DARK         0x00000020
#define    U_DENSITY_TOO_LIGHT        0x00000040
#define    U_DENSITY_LITTLE_LIGHT     0x00000060
#define    U_DENSITY_AMBIGUOUS        0x00000080

#define    U_INSUFFICIENT_FP          -31
#define    U_NOT_YET                  -32
            
#define    U_LEFT                     -41
#define    U_RIGHT                    -42
#define    U_UP                       -43
#define    U_DOWN                     -44
            
#define    U_CLASS_A                 65
#define    U_CLASS_B                 66
#define    U_CLASS_C                 67
#define    U_CLASS_D                 68
#define    U_CLASS_E                 69
#define    U_CLASS_R				 82

/*---------------------------------*\
|   Definition of security level    |
\*---------------------------------*/
#define    AUTO_SECURITY		  0
#define    SECURITY_A                   1
#define    SECURITY_B                   2
#define    SECURITY_C                   3
#define    SECURITY_D                   4
#define    SECURITY_E                   5
#define    SECURITY_M                  30
#define    SECURITY_R                   40

#ifdef FM220
#define    FP_IMAGE_WIDTH             256
#define    FP_IMAGE_HEIGHT            324
#endif
#ifdef FM300
#define    FP_IMAGE_WIDTH             360
#define    FP_IMAGE_HEIGHT            400
#endif
#ifdef SFC160S_SERIES
#define    FP_IMAGE_WIDTH             160
#define    FP_IMAGE_HEIGHT            160
#endif
#ifdef SFC360
#define    FP_IMAGE_WIDTH             256
#define    FP_IMAGE_HEIGHT            360
#endif

#define    GRAY_IMAGE_SIZE        (((long)FP_IMAGE_WIDTH)*(FP_IMAGE_HEIGHT))
#define    BIN_IMAGE_SIZE          (GRAY_IMAGE_SIZE/8)

#define		LARGE								10
#define		SMALL								11
#define    RAW							  12	
#define    BMP							   13	

#define    GRAY_IMAGE             8
#define    BIN_IMAGE                  1

#define    GRAY_LEVEL                 256
#define    GRAY_STEP     (256/GRAY_LEVEL)

/*---------------------------------*\
|   Definition of enrollment mode   |
\*---------------------------------*/
#define    DEFAULT_MODE              0x00
#define    FP_CODE_LENGTH             256
#ifdef FM_SERIES
#define    ISO_CODE_LENGTH             512
#endif
#ifdef SFC160S_SERIES
#define    ISO_CODE_LENGTH             8196
#endif

#define    Creat_FP_IMAGE_SET   Create_FP_IMAGE_SET
#define    Creat_FP_ENRL_SET    Create_FP_ENRL_SET

/*---------------------------------*\
|   Fingerprint image information   |
\*---------------------------------*/
typedef struct FP_Header {
    BYTE   FP_sig[2];
    long            size;
    short           width;
    short           height;
    BYTE   bit_per_pixel;
    BYTE   compression;
    BYTE   P_version;
 	BYTE   valid;
    BYTE   reserved;
    BYTE   chksum;
} FP_HEADER;

typedef struct FP_Image_Set {
    FP_HEADER                header;
    BYTE *     image;
} FP_IMAGE_SET;

/*----------------------------------------*\
|   Structure definition for enrollment    |
\*----------------------------------------*/
typedef struct
{
    short               quality;
    short               percentage;
    short               probability;    
    BYTE       fp_code[ISO_CODE_LENGTH];
} FP_ENRL_SET;


typedef struct FP_BMP {
    LPBITMAPINFO            lpBmpInfo;                
    BYTE*     lpDib;
} FP_BMP_SET;

/*----------------------------------------*\
|   Declaration of API functions           |
\*----------------------------------------*/
#ifdef __cplusplus
extern "C"{
#endif

extern HCONNECT WINAPI FP_ConnectCaptureDriver(int reserved);
extern void WINAPI FP_DisconnectCaptureDriver( HCONNECT hConnect);


extern int WINAPI  FP_Snap(HCONNECT hConnect);

extern HFPCAPTURE WINAPI FP_CreateCaptureHandle( HCONNECT hConnect );
extern int WINAPI  FP_Capture(HCONNECT hConnect,HFPCAPTURE hFPCapture);
extern int WINAPI FP_DestroyCaptureHandle(HCONNECT hConnect,HFPCAPTURE hFPCapture);
#ifdef FM300
extern int WINAPI FP_GetImageQuality(HCONNECT);
#endif

//extern int WINAPI FP_GetPrimaryCode(HCONNECT hConnect, BYTE* pcode);

extern HFPIMAGE  WINAPI FP_CreateImageHandle( HCONNECT hConnect,BYTE bMode,WORD wSize);
extern int WINAPI FP_GetImage(HCONNECT hConnect,HFPIMAGE hFPImage);
extern int WINAPI FP_DestroyImageHandle(HCONNECT hConnect,HFPIMAGE hFPImage);

extern HFPENROLL WINAPI FP_CreateEnrollHandle( HCONNECT hConnect,BYTE reserved);
//extern int WINAPI FP_Enroll( HCONNECT hConnect,HFPENROLL hFPEnroll,BYTE *pcode,BYTE *fpcode);
extern int WINAPI FP_DestroyEnrollHandle( HCONNECT hConnect,HFPENROLL hFPEnroll);

extern int WINAPI FP_ImageMatch( HCONNECT hConnect,BYTE *fpcode,WORD wSecutity);
extern int WINAPI FP_CodeMatch( HCONNECT hConnect,BYTE *pcode, BYTE *fpcode,WORD wSecutity);
extern int WINAPI FP_ImageMatchEx( HCONNECT hConnect,BYTE *fpcode, WORD wSecutity,DWORD *dwScore);
extern int WINAPI FP_CodeMatchEx( HCONNECT hConnect,BYTE *pcode, BYTE *fpcode, WORD wSecutity, DWORD *dwScore );

extern int WINAPI FP_CheckBlank(HCONNECT hConnect);
extern int WINAPI FP_Diagnose(HCONNECT hConnect);

extern int WINAPI FP_SaveImage(HCONNECT hConnect,HFPIMAGE hFPImage,WORD wFileType,char *Filename);

/* extern HBITMAP WINAPI FP_GetBitmapHandle(HCONNECT hConnect,HDC,HFPIMAGE); */
extern void WINAPI FP_GetImageDimension(HCONNECT hConnect,int *nWidth,int *nHeight);
extern int WINAPI FP_FreezeImage(HCONNECT hConnect);
extern int WINAPI FP_DisplayImage(HCONNECT hConnect,HDC hDC,HFPIMAGE hFPImage,int nStartX,int nStartY,int nWidth,int nHeight);

//iso 19794-2 function
extern int WINAPI FP_GetTemplate(HANDLE hConnect,BYTE *minu_code,int mode,int key);
extern int WINAPI FP_GetEncryptedTemplate(HANDLE hConnect,BYTE *encrypted_minu_code,int mode,int key, BYTE *iv, BYTE *encrypted_skey);
extern int WINAPI FP_EnrollEx(HANDLE hConnect,HFPENROLL hEnrlSet,BYTE *pcode,BYTE *fpcode,int mode);
extern int WINAPI FP_EnrollEx_Encrypted(HANDLE hConnect, HFPENROLL hEnrlSet, BYTE *encrypted_fpcode, int mode, BYTE *iv, BYTE *encrypted_skey);

extern int WINAPI FP_SetPublicKey(HANDLE hConnect, BYTE *pPublicKey, int KeyLen);
extern int WINAPI FP_SetSessionKey(HANDLE hConnect, BYTE *pSessionKey);
extern int WINAPI FP_GetDeleteData(HANDLE hConnect,TCHAR *UserId, int fpIndex, BYTE *encDeleteData, int *p_enc_len);

extern int WINAPI FP_SetLowSpeed(void);

#ifdef __cplusplus
}
#endif
