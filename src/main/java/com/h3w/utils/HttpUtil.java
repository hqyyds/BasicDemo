package com.h3w.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpUtil {
    private final static Logger logger = Logger.getLogger(HttpUtil.class);
    public static Map<String, String> contentTypes;

    static {
        contentTypes = new HashMap<String, String>();
        contentTypes.put(".load", "text/html");
        contentTypes.put(".123", "application/vnd.lotus-1-2-3");
        contentTypes.put(".3ds", "image/x-3ds");
        contentTypes.put(".3g2", "video/3gpp");
        contentTypes.put(".3ga", "video/3gpp");
        contentTypes.put(".3gp", "video/3gpp");
        contentTypes.put(".3gpp", "video/3gpp");
        contentTypes.put(".602", "application/x-t602");
        contentTypes.put(".669", "audio/x-mod");
        contentTypes.put(".7z", "application/x-7z-compressed");
        contentTypes.put(".a", "application/x-archive");
        contentTypes.put(".aac", "audio/mp4");
        contentTypes.put(".abw", "application/x-abiword");
        contentTypes.put(".abw.crashed", "application/x-abiword");
        contentTypes.put(".abw.gz", "application/x-abiword");
        contentTypes.put(".ac3", "audio/ac3");
        contentTypes.put(".ace", "application/x-ace");
        contentTypes.put(".adb", "text/x-adasrc");
        contentTypes.put(".ads", "text/x-adasrc");
        contentTypes.put(".afm", "application/x-font-afm");
        contentTypes.put(".ag", "image/x-applix-graphics");
        contentTypes.put(".ai", "application/illustrator");
        contentTypes.put(".aif", "audio/x-aiff");
        contentTypes.put(".aifc", "audio/x-aiff");
        contentTypes.put(".aiff", "audio/x-aiff");
        contentTypes.put(".al", "application/x-perl");
        contentTypes.put(".alz", "application/x-alz");
        contentTypes.put(".amr", "audio/amr");
        contentTypes.put(".ani", "application/x-navi-animation");
        contentTypes.put(".anim[1-9j]", "video/x-anim");
        contentTypes.put(".anx", "application/annodex");
        contentTypes.put(".ape", "audio/x-ape");
        contentTypes.put(".arj", "application/x-arj");
        contentTypes.put(".arw", "image/x-sony-arw");
        contentTypes.put(".as", "application/x-applix-spreadsheet");
        contentTypes.put(".asc", "text/plain");
        contentTypes.put(".asf", "video/x-ms-asf");
        contentTypes.put(".asp", "application/x-asp");
        contentTypes.put(".ass", "text/x-ssa");
        contentTypes.put(".asx", "audio/x-ms-asx");
        contentTypes.put(".atom", "application/atom+xml");
        contentTypes.put(".au", "audio/basic");
        contentTypes.put(".avi", "video/x-msvideo");
        contentTypes.put(".aw", "application/x-applix-word");
        contentTypes.put(".awb", "audio/amr-wb");
        contentTypes.put(".awk", "application/x-awk");
        contentTypes.put(".axa", "audio/annodex");
        contentTypes.put(".axv", "video/annodex");
        contentTypes.put(".bak", "application/x-trash");
        contentTypes.put(".bcpio", "application/x-bcpio");
        contentTypes.put(".bdf", "application/x-font-bdf");
        contentTypes.put(".bib", "text/x-bibtex");
        contentTypes.put(".bin", "application/octet-stream");
        contentTypes.put(".blend", "application/x-blender");
        contentTypes.put(".blender", "application/x-blender");
        contentTypes.put(".bmp", "image/bmp");
        contentTypes.put(".bz", "application/x-bzip");
        contentTypes.put(".bz2", "application/x-bzip");
        contentTypes.put(".c", "text/x-csrc");
        contentTypes.put(".c++", "text/x-c++src");
        contentTypes.put(".cab", "application/vnd.ms-cab-compressed");
        contentTypes.put(".cb7", "application/x-cb7");
        contentTypes.put(".cbr", "application/x-cbr");
        contentTypes.put(".cbt", "application/x-cbt");
        contentTypes.put(".cbz", "application/x-cbz");
        contentTypes.put(".cc", "text/x-c++src");
        contentTypes.put(".cdf", "application/x-netcdf");
        contentTypes.put(".cdr", "application/vnd.corel-draw");
        contentTypes.put(".cer", "application/x-x509-ca-cert");
        contentTypes.put(".cert", "application/x-x509-ca-cert");
        contentTypes.put(".cgm", "image/cgm");
        contentTypes.put(".chm", "application/x-chm");
        contentTypes.put(".chrt", "application/x-kchart");
        contentTypes.put(".class", "application/x-java");
        contentTypes.put(".cls", "text/x-tex");
        contentTypes.put(".cmake", "text/x-cmake");
        contentTypes.put(".cpio", "application/x-cpio");
        contentTypes.put(".cpio.gz", "application/x-cpio-compressed");
        contentTypes.put(".cpp", "text/x-c++src");
        contentTypes.put(".cr2", "image/x-canon-cr2");
        contentTypes.put(".crt", "application/x-x509-ca-cert");
        contentTypes.put(".crw", "image/x-canon-crw");
        contentTypes.put(".cs", "text/x-csharp");
        contentTypes.put(".csh", "application/x-csh");
        contentTypes.put(".css", "text/css");
        contentTypes.put(".cssl", "text/css");
        contentTypes.put(".csv", "text/csv");
        contentTypes.put(".cue", "application/x-cue");
        contentTypes.put(".cur", "image/x-win-bitmap");
        contentTypes.put(".cxx", "text/x-c++src");
        contentTypes.put(".d", "text/x-dsrc");
        contentTypes.put(".dar", "application/x-dar");
        contentTypes.put(".dbf", "application/x-dbf");
        contentTypes.put(".dc", "application/x-dc-rom");
        contentTypes.put(".dcl", "text/x-dcl");
        contentTypes.put(".dcm", "application/dicom");
        contentTypes.put(".dcr", "image/x-kodak-dcr");
        contentTypes.put(".dds", "image/x-dds");
        contentTypes.put(".deb", "application/x-deb");
        contentTypes.put(".der", "application/x-x509-ca-cert");
        contentTypes.put(".desktop", "application/x-desktop");
        contentTypes.put(".dia", "application/x-dia-diagram");
        contentTypes.put(".diff", "text/x-patch");
        contentTypes.put(".divx", "video/x-msvideo");
        contentTypes.put(".djv", "image/vnd.djvu");
        contentTypes.put(".djvu", "image/vnd.djvu");
        contentTypes.put(".dng", "image/x-adobe-dng");
        contentTypes.put(".doc", "application/msword");
        contentTypes.put(".docbook", "application/docbook+xml");
        contentTypes.put(".docm", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        contentTypes.put(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        contentTypes.put(".dot", "text/vnd.graphviz");
        contentTypes.put(".dsl", "text/x-dsl");
        contentTypes.put(".dtd", "application/xml-dtd");
        contentTypes.put(".dtx", "text/x-tex");
        contentTypes.put(".dv", "video/dv");
        contentTypes.put(".dvi", "application/x-dvi");
        contentTypes.put(".dvi.bz2", "application/x-bzdvi");
        contentTypes.put(".dvi.gz", "application/x-gzdvi");
        contentTypes.put(".dwg", "image/vnd.dwg");
        contentTypes.put(".dxf", "image/vnd.dxf");
        contentTypes.put(".e", "text/x-eiffel");
        contentTypes.put(".egon", "application/x-egon");
        contentTypes.put(".eif", "text/x-eiffel");
        contentTypes.put(".el", "text/x-emacs-lisp");
        contentTypes.put(".emf", "image/x-emf");
        contentTypes.put(".emp", "application/vnd.emusic-emusic_package");
        contentTypes.put(".ent", "application/xml-external-parsed-entity");
        contentTypes.put(".eps", "image/x-eps");
        contentTypes.put(".eps.bz2", "image/x-bzeps");
        contentTypes.put(".eps.gz", "image/x-gzeps");
        contentTypes.put(".epsf", "image/x-eps");
        contentTypes.put(".epsf.bz2", "image/x-bzeps");
        contentTypes.put(".epsf.gz", "image/x-gzeps");
        contentTypes.put(".epsi", "image/x-eps");
        contentTypes.put(".epsi.bz2", "image/x-bzeps");
        contentTypes.put(".epsi.gz", "image/x-gzeps");
        contentTypes.put(".epub", "application/epub+zip");
        contentTypes.put(".erl", "text/x-erlang");
        contentTypes.put(".es", "application/ecmascript");
        contentTypes.put(".etheme", "application/x-e-theme");
        contentTypes.put(".etx", "text/x-setext");
        contentTypes.put(".exe", "application/x-ms-dos-executable");
        contentTypes.put(".exr", "image/x-exr");
        contentTypes.put(".ez", "application/andrew-inset");
        contentTypes.put(".f", "text/x-fortran");
        contentTypes.put(".f90", "text/x-fortran");
        contentTypes.put(".f95", "text/x-fortran");
        contentTypes.put(".fb2", "application/x-fictionbook+xml");
        contentTypes.put(".fig", "image/x-xfig");
        contentTypes.put(".fits", "image/fits");
        contentTypes.put(".fl", "application/x-fluid");
        contentTypes.put(".flac", "audio/x-flac");
        contentTypes.put(".flc", "video/x-flic");
        contentTypes.put(".fli", "video/x-flic");
        contentTypes.put(".flv", "video/x-flv");
        contentTypes.put(".flw", "application/x-kivio");
        contentTypes.put(".fo", "text/x-xslfo");
        contentTypes.put(".for", "text/x-fortran");
        contentTypes.put(".g3", "image/fax-g3");
        contentTypes.put(".gb", "application/x-gameboy-rom");
        contentTypes.put(".gba", "application/x-gba-rom");
        contentTypes.put(".gcrd", "text/directory");
        contentTypes.put(".ged", "application/x-gedcom");
        contentTypes.put(".gedcom", "application/x-gedcom");
        contentTypes.put(".gen", "application/x-genesis-rom");
        contentTypes.put(".gf", "application/x-tex-gf");
        contentTypes.put(".gg", "application/x-sms-rom");
        contentTypes.put(".gif", "image/gif");
        contentTypes.put(".glade", "application/x-glade");
        contentTypes.put(".gmo", "application/x-gettext-translation");
        contentTypes.put(".gnc", "application/x-gnucash");
        contentTypes.put(".gnd", "application/gnunet-directory");
        contentTypes.put(".gnucash", "application/x-gnucash");
        contentTypes.put(".gnumeric", "application/x-gnumeric");
        contentTypes.put(".gnuplot", "application/x-gnuplot");
        contentTypes.put(".gp", "application/x-gnuplot");
        contentTypes.put(".gpg", "application/pgp-encrypted");
        contentTypes.put(".gplt", "application/x-gnuplot");
        contentTypes.put(".gra", "application/x-graphite");
        contentTypes.put(".gsf", "application/x-font-type1");
        contentTypes.put(".gsm", "audio/x-gsm");
        contentTypes.put(".gtar", "application/x-tar");
        contentTypes.put(".gv", "text/vnd.graphviz");
        contentTypes.put(".gvp", "text/x-google-video-pointer");
        contentTypes.put(".gz", "application/x-gzip");
        contentTypes.put(".h", "text/x-chdr");
        contentTypes.put(".h++", "text/x-c++hdr");
        contentTypes.put(".hdf", "application/x-hdf");
        contentTypes.put(".hh", "text/x-c++hdr");
        contentTypes.put(".hp", "text/x-c++hdr");
        contentTypes.put(".hpgl", "application/vnd.hp-hpgl");
        contentTypes.put(".hpp", "text/x-c++hdr");
        contentTypes.put(".hs", "text/x-haskell");
        contentTypes.put(".htm", "text/html");
        contentTypes.put(".html", "text/html");
        contentTypes.put(".hwp", "application/x-hwp");
        contentTypes.put(".hwt", "application/x-hwt");
        contentTypes.put(".hxx", "text/x-c++hdr");
        contentTypes.put(".ica", "application/x-ica");
        contentTypes.put(".icb", "image/x-tga");
        contentTypes.put(".icns", "image/x-icns");
        contentTypes.put(".ico", "image/vnd.microsoft.icon");
        contentTypes.put(".ics", "text/calendar");
        contentTypes.put(".idl", "text/x-idl");
        contentTypes.put(".ief", "image/ief");
        contentTypes.put(".iff", "image/x-iff");
        contentTypes.put(".ilbm", "image/x-ilbm");
        contentTypes.put(".ime", "text/x-imelody");
        contentTypes.put(".imy", "text/x-imelody");
        contentTypes.put(".ins", "text/x-tex");
        contentTypes.put(".iptables", "text/x-iptables");
        contentTypes.put(".iso", "application/x-cd-image");
        contentTypes.put(".iso9660", "application/x-cd-image");
        contentTypes.put(".it", "audio/x-it");
        contentTypes.put(".j2k", "image/jp2");
        contentTypes.put(".jad", "text/vnd.sun.j2me.app-descriptor");
        contentTypes.put(".jar", "application/x-java-archive");
        contentTypes.put(".java", "text/x-java");
        contentTypes.put(".jng", "image/x-jng");
        contentTypes.put(".jnlp", "application/x-java-jnlp-file");
        contentTypes.put(".jp2", "image/jp2");
        contentTypes.put(".jpc", "image/jp2");
        contentTypes.put(".jpe", "image/jpeg");
        contentTypes.put(".jpeg", "image/jpeg");
        contentTypes.put(".jpf", "image/jp2");
        contentTypes.put(".jpg", "image/jpeg");
        contentTypes.put(".jpr", "application/x-jbuilder-project");
        contentTypes.put(".jpx", "image/jp2");
        contentTypes.put(".js", "application/javascript");
        contentTypes.put(".json", "application/json");
        contentTypes.put(".jsonp", "application/jsonp");
        contentTypes.put(".k25", "image/x-kodak-k25");
        contentTypes.put(".kar", "audio/midi");
        contentTypes.put(".karbon", "application/x-karbon");
        contentTypes.put(".kdc", "image/x-kodak-kdc");
        contentTypes.put(".kdelnk", "application/x-desktop");
        contentTypes.put(".kexi", "application/x-kexiproject-sqlite3");
        contentTypes.put(".kexic", "application/x-kexi-connectiondata");
        contentTypes.put(".kexis", "application/x-kexiproject-shortcut");
        contentTypes.put(".kfo", "application/x-kformula");
        contentTypes.put(".kil", "application/x-killustrator");
        contentTypes.put(".kino", "application/smil");
        contentTypes.put(".kml", "application/vnd.google-earth.kml+xml");
        contentTypes.put(".kmz", "application/vnd.google-earth.kmz");
        contentTypes.put(".kon", "application/x-kontour");
        contentTypes.put(".kpm", "application/x-kpovmodeler");
        contentTypes.put(".kpr", "application/x-kpresenter");
        contentTypes.put(".kpt", "application/x-kpresenter");
        contentTypes.put(".kra", "application/x-krita");
        contentTypes.put(".ksp", "application/x-kspread");
        contentTypes.put(".kud", "application/x-kugar");
        contentTypes.put(".kwd", "application/x-kword");
        contentTypes.put(".kwt", "application/x-kword");
        contentTypes.put(".la", "application/x-shared-library-la");
        contentTypes.put(".latex", "text/x-tex");
        contentTypes.put(".ldif", "text/x-ldif");
        contentTypes.put(".lha", "application/x-lha");
        contentTypes.put(".lhs", "text/x-literate-haskell");
        contentTypes.put(".lhz", "application/x-lhz");
        contentTypes.put(".log", "text/x-log");
        contentTypes.put(".ltx", "text/x-tex");
        contentTypes.put(".lua", "text/x-lua");
        contentTypes.put(".lwo", "image/x-lwo");
        contentTypes.put(".lwob", "image/x-lwo");
        contentTypes.put(".lws", "image/x-lws");
        contentTypes.put(".ly", "text/x-lilypond");
        contentTypes.put(".lyx", "application/x-lyx");
        contentTypes.put(".lz", "application/x-lzip");
        contentTypes.put(".lzh", "application/x-lha");
        contentTypes.put(".lzma", "application/x-lzma");
        contentTypes.put(".lzo", "application/x-lzop");
        contentTypes.put(".m", "text/x-matlab");
        contentTypes.put(".m15", "audio/x-mod");
        contentTypes.put(".m2t", "video/mpeg");
        contentTypes.put(".m3u", "audio/x-mpegurl");
        contentTypes.put(".m3u8", "audio/x-mpegurl");
        contentTypes.put(".m4", "application/x-m4");
        contentTypes.put(".m4a", "audio/mp4");
        contentTypes.put(".m4b", "audio/x-m4b");
        contentTypes.put(".m4v", "video/mp4");
        contentTypes.put(".mab", "application/x-markaby");
        contentTypes.put(".man", "application/x-troff-man");
        contentTypes.put(".mbox", "application/mbox");
        contentTypes.put(".md", "application/x-genesis-rom");
        contentTypes.put(".mdb", "application/vnd.ms-access");
        contentTypes.put(".mdi", "image/vnd.ms-modi");
        contentTypes.put(".me", "text/x-troff-me");
        contentTypes.put(".med", "audio/x-mod");
        contentTypes.put(".metalink", "application/metalink+xml");
        contentTypes.put(".mgp", "application/x-magicpoint");
        contentTypes.put(".mid", "audio/midi");
        contentTypes.put(".midi", "audio/midi");
        contentTypes.put(".mif", "application/x-mif");
        contentTypes.put(".minipsf", "audio/x-minipsf");
        contentTypes.put(".mka", "audio/x-matroska");
        contentTypes.put(".mkv", "video/x-matroska");
        contentTypes.put(".ml", "text/x-ocaml");
        contentTypes.put(".mli", "text/x-ocaml");
        contentTypes.put(".mm", "text/x-troff-mm");
        contentTypes.put(".mmf", "application/x-smaf");
        contentTypes.put(".mml", "text/mathml");
        contentTypes.put(".mng", "video/x-mng");
        contentTypes.put(".mo", "application/x-gettext-translation");
        contentTypes.put(".mo3", "audio/x-mo3");
        contentTypes.put(".moc", "text/x-moc");
        contentTypes.put(".mod", "audio/x-mod");
        contentTypes.put(".mof", "text/x-mof");
        contentTypes.put(".moov", "video/quicktime");
        contentTypes.put(".mov", "video/quicktime");
        contentTypes.put(".movie", "video/x-sgi-movie");
        contentTypes.put(".mp+", "audio/x-musepack");
        contentTypes.put(".mp2", "video/mpeg");
        contentTypes.put(".mp3", "audio/mpeg");
        contentTypes.put(".mp4", "video/mp4");
        contentTypes.put(".mpc", "audio/x-musepack");
        contentTypes.put(".mpe", "video/mpeg");
        contentTypes.put(".mpeg", "video/mpeg");
        contentTypes.put(".mpg", "video/mpeg");
        contentTypes.put(".mpga", "audio/mpeg");
        contentTypes.put(".mpp", "audio/x-musepack");
        contentTypes.put(".mrl", "text/x-mrml");
        contentTypes.put(".mrml", "text/x-mrml");
        contentTypes.put(".mrw", "image/x-minolta-mrw");
        contentTypes.put(".ms", "text/x-troff-ms");
        contentTypes.put(".msi", "application/x-msi");
        contentTypes.put(".msod", "image/x-msod");
        contentTypes.put(".msx", "application/x-msx-rom");
        contentTypes.put(".mtm", "audio/x-mod");
        contentTypes.put(".mup", "text/x-mup");
        contentTypes.put(".mxf", "application/mxf");
        contentTypes.put(".n64", "application/x-n64-rom");
        contentTypes.put(".nb", "application/mathematica");
        contentTypes.put(".nc", "application/x-netcdf");
        contentTypes.put(".nds", "application/x-nintendo-ds-rom");
        contentTypes.put(".nef", "image/x-nikon-nef");
        contentTypes.put(".nes", "application/x-nes-rom");
        contentTypes.put(".nfo", "text/x-nfo");
        contentTypes.put(".not", "text/x-mup");
        contentTypes.put(".nsc", "application/x-netshow-channel");
        contentTypes.put(".nsv", "video/x-nsv");
        contentTypes.put(".o", "application/x-object");
        contentTypes.put(".obj", "application/x-tgif");
        contentTypes.put(".ocl", "text/x-ocl");
        contentTypes.put(".oda", "application/oda");
        contentTypes.put(".odb", "application/vnd.oasis.opendocument.database");
        contentTypes.put(".odc", "application/vnd.oasis.opendocument.chart");
        contentTypes.put(".odf", "application/vnd.oasis.opendocument.formula");
        contentTypes.put(".odg", "application/vnd.oasis.opendocument.graphics");
        contentTypes.put(".odi", "application/vnd.oasis.opendocument.image");
        contentTypes.put(".odm", "application/vnd.oasis.opendocument.text-master");
        contentTypes.put(".odp", "application/vnd.oasis.opendocument.presentation");
        contentTypes.put(".ods", "application/vnd.oasis.opendocument.spreadsheet");
        contentTypes.put(".odt", "application/vnd.oasis.opendocument.text");
        contentTypes.put(".oga", "audio/ogg");
        contentTypes.put(".ogg", "video/x-theora+ogg");
        contentTypes.put(".ogm", "video/x-ogm+ogg");
        contentTypes.put(".ogv", "video/ogg");
        contentTypes.put(".ogx", "application/ogg");
        contentTypes.put(".old", "application/x-trash");
        contentTypes.put(".oleo", "application/x-oleo");
        contentTypes.put(".opml", "text/x-opml+xml");
        contentTypes.put(".ora", "image/openraster");
        contentTypes.put(".orf", "image/x-olympus-orf");
        contentTypes.put(".otc", "application/vnd.oasis.opendocument.chart-template");
        contentTypes.put(".otf", "application/x-font-otf");
        contentTypes.put(".otg", "application/vnd.oasis.opendocument.graphics-template");
        contentTypes.put(".oth", "application/vnd.oasis.opendocument.text-web");
        contentTypes.put(".otp", "application/vnd.oasis.opendocument.presentation-template");
        contentTypes.put(".ots", "application/vnd.oasis.opendocument.spreadsheet-template");
        contentTypes.put(".ott", "application/vnd.oasis.opendocument.text-template");
        contentTypes.put(".owl", "application/rdf+xml");
        contentTypes.put(".oxt", "application/vnd.openofficeorg.extension");
        contentTypes.put(".p", "text/x-pascal");
        contentTypes.put(".p10", "application/pkcs10");
        contentTypes.put(".p12", "application/x-pkcs12");
        contentTypes.put(".p7b", "application/x-pkcs7-certificates");
        contentTypes.put(".p7s", "application/pkcs7-signature");
        contentTypes.put(".pack", "application/x-java-pack200");
        contentTypes.put(".pak", "application/x-pak");
        contentTypes.put(".par2", "application/x-par2");
        contentTypes.put(".pas", "text/x-pascal");
        contentTypes.put(".patch", "text/x-patch");
        contentTypes.put(".pbm", "image/x-portable-bitmap");
        contentTypes.put(".pcd", "image/x-photo-cd");
        contentTypes.put(".pcf", "application/x-cisco-vpn-settings");
        contentTypes.put(".pcf.gz", "application/x-font-pcf");
        contentTypes.put(".pcf.z", "application/x-font-pcf");
        contentTypes.put(".pcl", "application/vnd.hp-pcl");
        contentTypes.put(".pcx", "image/x-pcx");
        contentTypes.put(".pdb", "chemical/x-pdb");
        contentTypes.put(".pdc", "application/x-aportisdoc");
        contentTypes.put(".pdf", "application/pdf");
        contentTypes.put(".pdf.bz2", "application/x-bzpdf");
        contentTypes.put(".pdf.gz", "application/x-gzpdf");
        contentTypes.put(".pef", "image/x-pentax-pef");
        contentTypes.put(".pem", "application/x-x509-ca-cert");
        contentTypes.put(".perl", "application/x-perl");
        contentTypes.put(".pfa", "application/x-font-type1");
        contentTypes.put(".pfb", "application/x-font-type1");
        contentTypes.put(".pfx", "application/x-pkcs12");
        contentTypes.put(".pgm", "image/x-portable-graymap");
        contentTypes.put(".pgn", "application/x-chess-pgn");
        contentTypes.put(".pgp", "application/pgp-encrypted");
        contentTypes.put(".php", "application/x-php");
        contentTypes.put(".php3", "application/x-php");
        contentTypes.put(".php4", "application/x-php");
        contentTypes.put(".pict", "image/x-pict");
        contentTypes.put(".pict1", "image/x-pict");
        contentTypes.put(".pict2", "image/x-pict");
        contentTypes.put(".pickle", "application/python-pickle");
        contentTypes.put(".pk", "application/x-tex-pk");
        contentTypes.put(".pkipath", "application/pkix-pkipath");
        contentTypes.put(".pkr", "application/pgp-keys");
        contentTypes.put(".pl", "application/x-perl");
        contentTypes.put(".pla", "audio/x-iriver-pla");
        contentTypes.put(".pln", "application/x-planperfect");
        contentTypes.put(".pls", "audio/x-scpls");
        contentTypes.put(".pm", "application/x-perl");
        contentTypes.put(".png", "image/png");
        contentTypes.put(".pnm", "image/x-portable-anymap");
        contentTypes.put(".pntg", "image/x-macpaint");
        contentTypes.put(".po", "text/x-gettext-translation");
        contentTypes.put(".por", "application/x-spss-por");
        contentTypes.put(".pot", "text/x-gettext-translation-template");
        contentTypes.put(".ppm", "image/x-portable-pixmap");
        contentTypes.put(".pps", "application/vnd.ms-powerpoint");
        contentTypes.put(".ppt", "application/vnd.ms-powerpoint");
        contentTypes.put(".pptm", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        contentTypes.put(".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        contentTypes.put(".ppz", "application/vnd.ms-powerpoint");
        contentTypes.put(".prc", "application/x-palm-database");
        contentTypes.put(".ps", "application/postscript");
        contentTypes.put(".ps.bz2", "application/x-bzpostscript");
        contentTypes.put(".ps.gz", "application/x-gzpostscript");
        contentTypes.put(".psd", "image/vnd.adobe.photoshop");
        contentTypes.put(".psf", "audio/x-psf");
        contentTypes.put(".psf.gz", "application/x-gz-font-linux-psf");
        contentTypes.put(".psflib", "audio/x-psflib");
        contentTypes.put(".psid", "audio/prs.sid");
        contentTypes.put(".psw", "application/x-pocket-word");
        contentTypes.put(".pw", "application/x-pw");
        contentTypes.put(".py", "text/x-python");
        contentTypes.put(".pyc", "application/x-python-bytecode");
        contentTypes.put(".pyo", "application/x-python-bytecode");
        contentTypes.put(".qif", "image/x-quicktime");
        contentTypes.put(".qt", "video/quicktime");
        contentTypes.put(".qtif", "image/x-quicktime");
        contentTypes.put(".qtl", "application/x-quicktime-media-link");
        contentTypes.put(".qtvr", "video/quicktime");
        contentTypes.put(".ra", "audio/vnd.rn-realaudio");
        contentTypes.put(".raf", "image/x-fuji-raf");
        contentTypes.put(".ram", "application/ram");
        contentTypes.put(".rar", "application/x-rar");
        contentTypes.put(".ras", "image/x-cmu-raster");
        contentTypes.put(".raw", "image/x-panasonic-raw");
        contentTypes.put(".rax", "audio/vnd.rn-realaudio");
        contentTypes.put(".rb", "application/x-ruby");
        contentTypes.put(".rdf", "application/rdf+xml");
        contentTypes.put(".rdfs", "application/rdf+xml");
        contentTypes.put(".reg", "text/x-ms-regedit");
        contentTypes.put(".rej", "application/x-reject");
        contentTypes.put(".rgb", "image/x-rgb");
        contentTypes.put(".rle", "image/rle");
        contentTypes.put(".rm", "application/vnd.rn-realmedia");
        contentTypes.put(".rmj", "application/vnd.rn-realmedia");
        contentTypes.put(".rmm", "application/vnd.rn-realmedia");
        contentTypes.put(".rms", "application/vnd.rn-realmedia");
        contentTypes.put(".rmvb", "application/vnd.rn-realmedia");
        contentTypes.put(".rmx", "application/vnd.rn-realmedia");
        contentTypes.put(".roff", "text/troff");
        contentTypes.put(".rp", "image/vnd.rn-realpix");
        contentTypes.put(".rpm", "application/x-rpm");
        contentTypes.put(".rss", "application/rss+xml");
        contentTypes.put(".rt", "text/vnd.rn-realtext");
        contentTypes.put(".rtf", "application/rtf");
        contentTypes.put(".rtx", "text/richtext");
        contentTypes.put(".rv", "video/vnd.rn-realvideo");
        contentTypes.put(".rvx", "video/vnd.rn-realvideo");
        contentTypes.put(".s3m", "audio/x-s3m");
        contentTypes.put(".sam", "application/x-amipro");
        contentTypes.put(".sami", "application/x-sami");
        contentTypes.put(".sav", "application/x-spss-sav");
        contentTypes.put(".scm", "text/x-scheme");
        contentTypes.put(".sda", "application/vnd.stardivision.draw");
        contentTypes.put(".sdc", "application/vnd.stardivision.calc");
        contentTypes.put(".sdd", "application/vnd.stardivision.impress");
        contentTypes.put(".sdp", "application/sdp");
        contentTypes.put(".sds", "application/vnd.stardivision.chart");
        contentTypes.put(".sdw", "application/vnd.stardivision.writer");
        contentTypes.put(".sgf", "application/x-go-sgf");
        contentTypes.put(".sgi", "image/x-sgi");
        contentTypes.put(".sgl", "application/vnd.stardivision.writer");
        contentTypes.put(".sgm", "text/sgml");
        contentTypes.put(".sgml", "text/sgml");
        contentTypes.put(".sh", "application/x-shellscript");
        contentTypes.put(".shar", "application/x-shar");
        contentTypes.put(".shn", "application/x-shorten");
        contentTypes.put(".siag", "application/x-siag");
        contentTypes.put(".sid", "audio/prs.sid");
        contentTypes.put(".sik", "application/x-trash");
        contentTypes.put(".sis", "application/vnd.symbian.install");
        contentTypes.put(".sisx", "x-epoc/x-sisx-app");
        contentTypes.put(".sit", "application/x-stuffit");
        contentTypes.put(".siv", "application/sieve");
        contentTypes.put(".sk", "image/x-skencil");
        contentTypes.put(".sk1", "image/x-skencil");
        contentTypes.put(".skr", "application/pgp-keys");
        contentTypes.put(".slk", "text/spreadsheet");
        contentTypes.put(".smaf", "application/x-smaf");
        contentTypes.put(".smc", "application/x-snes-rom");
        contentTypes.put(".smd", "application/vnd.stardivision.mail");
        contentTypes.put(".smf", "application/vnd.stardivision.math");
        contentTypes.put(".smi", "application/x-sami");
        contentTypes.put(".smil", "application/smil");
        contentTypes.put(".sml", "application/smil");
        contentTypes.put(".sms", "application/x-sms-rom");
        contentTypes.put(".snd", "audio/basic");
        contentTypes.put(".so", "application/x-sharedlib");
        contentTypes.put(".spc", "application/x-pkcs7-certificates");
        contentTypes.put(".spd", "application/x-font-speedo");
        contentTypes.put(".spec", "text/x-rpm-spec");
        contentTypes.put(".spl", "application/x-shockwave-flash");
        contentTypes.put(".spx", "audio/x-speex");
        contentTypes.put(".sql", "text/x-sql");
        contentTypes.put(".sr2", "image/x-sony-sr2");
        contentTypes.put(".src", "application/x-wais-source");
        contentTypes.put(".srf", "image/x-sony-srf");
        contentTypes.put(".srt", "application/x-subrip");
        contentTypes.put(".ssa", "text/x-ssa");
        contentTypes.put(".stc", "application/vnd.sun.xml.calc.template");
        contentTypes.put(".std", "application/vnd.sun.xml.draw.template");
        contentTypes.put(".sti", "application/vnd.sun.xml.impress.template");
        contentTypes.put(".stm", "audio/x-stm");
        contentTypes.put(".stw", "application/vnd.sun.xml.writer.template");
        contentTypes.put(".sty", "text/x-tex");
        contentTypes.put(".sub", "text/x-subviewer");
        contentTypes.put(".sun", "image/x-sun-raster");
        contentTypes.put(".sv4cpio", "application/x-sv4cpio");
        contentTypes.put(".sv4crc", "application/x-sv4crc");
        contentTypes.put(".svg", "image/svg+xml");
        contentTypes.put(".svgz", "image/svg+xml-compressed");
        contentTypes.put(".swf", "application/x-shockwave-flash");
        contentTypes.put(".sxc", "application/vnd.sun.xml.calc");
        contentTypes.put(".sxd", "application/vnd.sun.xml.draw");
        contentTypes.put(".sxg", "application/vnd.sun.xml.writer.global");
        contentTypes.put(".sxi", "application/vnd.sun.xml.impress");
        contentTypes.put(".sxm", "application/vnd.sun.xml.math");
        contentTypes.put(".sxw", "application/vnd.sun.xml.writer");
        contentTypes.put(".sylk", "text/spreadsheet");
        contentTypes.put(".t", "text/troff");
        contentTypes.put(".t2t", "text/x-txt2tags");
        contentTypes.put(".tar", "application/x-tar");
        contentTypes.put(".tar.bz", "application/x-bzip-compressed-tar");
        contentTypes.put(".tar.bz2", "application/x-bzip-compressed-tar");
        contentTypes.put(".tar.gz", "application/x-compressed-tar");
        contentTypes.put(".tar.lzma", "application/x-lzma-compressed-tar");
        contentTypes.put(".tar.lzo", "application/x-tzo");
        contentTypes.put(".tar.xz", "application/x-xz-compressed-tar");
        contentTypes.put(".tar.z", "application/x-tarz");
        contentTypes.put(".tbz", "application/x-bzip-compressed-tar");
        contentTypes.put(".tbz2", "application/x-bzip-compressed-tar");
        contentTypes.put(".tcl", "text/x-tcl");
        contentTypes.put(".tex", "text/x-tex");
        contentTypes.put(".texi", "text/x-texinfo");
        contentTypes.put(".texinfo", "text/x-texinfo");
        contentTypes.put(".tga", "image/x-tga");
        contentTypes.put(".tgz", "application/x-compressed-tar");
        contentTypes.put(".theme", "application/x-theme");
        contentTypes.put(".themepack", "application/x-windows-themepack");
        contentTypes.put(".tif", "image/tiff");
        contentTypes.put(".tiff", "image/tiff");
        contentTypes.put(".tk", "text/x-tcl");
        contentTypes.put(".tlz", "application/x-lzma-compressed-tar");
        contentTypes.put(".tnef", "application/vnd.ms-tnef");
        contentTypes.put(".tnf", "application/vnd.ms-tnef");
        contentTypes.put(".toc", "application/x-cdrdao-toc");
        contentTypes.put(".torrent", "application/x-bittorrent");
        contentTypes.put(".tpic", "image/x-tga");
        contentTypes.put(".tr", "text/troff");
        contentTypes.put(".ts", "application/x-linguist");
        contentTypes.put(".tsv", "text/tab-separated-values");
        contentTypes.put(".tta", "audio/x-tta");
        contentTypes.put(".ttc", "application/x-font-ttf");
        contentTypes.put(".ttf", "application/x-font-ttf");
        contentTypes.put(".ttx", "application/x-font-ttx");
        contentTypes.put(".txt", "text/plain");
        contentTypes.put(".txz", "application/x-xz-compressed-tar");
        contentTypes.put(".tzo", "application/x-tzo");
        contentTypes.put(".ufraw", "application/x-ufraw");
        contentTypes.put(".ui", "application/x-designer");
        contentTypes.put(".uil", "text/x-uil");
        contentTypes.put(".ult", "audio/x-mod");
        contentTypes.put(".uni", "audio/x-mod");
        contentTypes.put(".uri", "text/x-uri");
        contentTypes.put(".url", "text/x-uri");
        contentTypes.put(".ustar", "application/x-ustar");
        contentTypes.put(".vala", "text/x-vala");
        contentTypes.put(".vapi", "text/x-vala");
        contentTypes.put(".vcf", "text/directory");
        contentTypes.put(".vcs", "text/calendar");
        contentTypes.put(".vct", "text/directory");
        contentTypes.put(".vda", "image/x-tga");
        contentTypes.put(".vhd", "text/x-vhdl");
        contentTypes.put(".vhdl", "text/x-vhdl");
        contentTypes.put(".viv", "video/vivo");
        contentTypes.put(".vivo", "video/vivo");
        contentTypes.put(".vlc", "audio/x-mpegurl");
        contentTypes.put(".vob", "video/mpeg");
        contentTypes.put(".voc", "audio/x-voc");
        contentTypes.put(".vor", "application/vnd.stardivision.writer");
        contentTypes.put(".vst", "image/x-tga");
        contentTypes.put(".wav", "audio/x-wav");
        contentTypes.put(".wax", "audio/x-ms-asx");
        contentTypes.put(".wb1", "application/x-quattropro");
        contentTypes.put(".wb2", "application/x-quattropro");
        contentTypes.put(".wb3", "application/x-quattropro");
        contentTypes.put(".wbmp", "image/vnd.wap.wbmp");
        contentTypes.put(".wcm", "application/vnd.ms-works");
        contentTypes.put(".wdb", "application/vnd.ms-works");
        contentTypes.put(".webm", "video/webm");
        contentTypes.put(".wk1", "application/vnd.lotus-1-2-3");
        contentTypes.put(".wk3", "application/vnd.lotus-1-2-3");
        contentTypes.put(".wk4", "application/vnd.lotus-1-2-3");
        contentTypes.put(".wks", "application/vnd.ms-works");
        contentTypes.put(".wma", "audio/x-ms-wma");
        contentTypes.put(".wmf", "image/x-wmf");
        contentTypes.put(".wml", "text/vnd.wap.wml");
        contentTypes.put(".wmls", "text/vnd.wap.wmlscript");
        contentTypes.put(".wmv", "video/x-ms-wmv");
        contentTypes.put(".wmx", "audio/x-ms-asx");
        contentTypes.put(".wp", "application/vnd.wordperfect");
        contentTypes.put(".wp4", "application/vnd.wordperfect");
        contentTypes.put(".wp5", "application/vnd.wordperfect");
        contentTypes.put(".wp6", "application/vnd.wordperfect");
        contentTypes.put(".wpd", "application/vnd.wordperfect");
        contentTypes.put(".wpg", "application/x-wpg");
        contentTypes.put(".wpl", "application/vnd.ms-wpl");
        contentTypes.put(".wpp", "application/vnd.wordperfect");
        contentTypes.put(".wps", "application/vnd.ms-works");
        contentTypes.put(".wri", "application/x-mswrite");
        contentTypes.put(".wrl", "model/vrml");
        contentTypes.put(".wv", "audio/x-wavpack");
        contentTypes.put(".wvc", "audio/x-wavpack-correction");
        contentTypes.put(".wvp", "audio/x-wavpack");
        contentTypes.put(".wvx", "audio/x-ms-asx");
        contentTypes.put(".x3f", "image/x-sigma-x3f");
        contentTypes.put(".xac", "application/x-gnucash");
        contentTypes.put(".xbel", "application/x-xbel");
        contentTypes.put(".xbl", "application/xml");
        contentTypes.put(".xbm", "image/x-xbitmap");
        contentTypes.put(".xcf", "image/x-xcf");
        contentTypes.put(".xcf.bz2", "image/x-compressed-xcf");
        contentTypes.put(".xcf.gz", "image/x-compressed-xcf");
        contentTypes.put(".xhtml", "application/xhtml+xml");
        contentTypes.put(".xi", "audio/x-xi");
        contentTypes.put(".xla", "application/vnd.ms-excel");
        contentTypes.put(".xlc", "application/vnd.ms-excel");
        contentTypes.put(".xld", "application/vnd.ms-excel");
        contentTypes.put(".xlf", "application/x-xliff");
        contentTypes.put(".xliff", "application/x-xliff");
        contentTypes.put(".xll", "application/vnd.ms-excel");
        contentTypes.put(".xlm", "application/vnd.ms-excel");
        contentTypes.put(".xls", "application/vnd.ms-excel");
        contentTypes.put(".xlsm", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        contentTypes.put(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        contentTypes.put(".xlt", "application/vnd.ms-excel");
        contentTypes.put(".xlw", "application/vnd.ms-excel");
        contentTypes.put(".xm", "audio/x-xm");
        contentTypes.put(".xmf", "audio/x-xmf");
        contentTypes.put(".xmi", "text/x-xmi");
        contentTypes.put(".xml", "application/xml");
        contentTypes.put(".xpm", "image/x-xpixmap");
        contentTypes.put(".xps", "application/vnd.ms-xpsdocument");
        contentTypes.put(".xsl", "application/xml");
        contentTypes.put(".xslfo", "text/x-xslfo");
        contentTypes.put(".xslt", "application/xml");
        contentTypes.put(".xspf", "application/xspf+xml");
        contentTypes.put(".xul", "application/vnd.mozilla.xul+xml");
        contentTypes.put(".xwd", "image/x-xwindowdump");
        contentTypes.put(".xyz", "chemical/x-pdb");
        contentTypes.put(".xz", "application/x-xz");
        contentTypes.put(".w2p", "application/w2p");
        contentTypes.put(".z", "application/x-compress");
        contentTypes.put(".zabw", "application/x-abiword");
        contentTypes.put(".zip", "application/zip");
        contentTypes.put(".zoo", "application/x-zoo");

    }

    public static String httpGet(String url) {
        //创建HttpClientBuilder
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        //HttpClient
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(3000)   //设置连接超时时间
                .setConnectionRequestTimeout(3000) // 设置请求超时时间
                .setSocketTimeout(3000)
                .setRedirectsEnabled(true)//默认允许自动重定向
                .build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        try {
            logger.info(httpGet.getRequestLine());
            //执行get请求
            HttpResponse httpResponse = closeableHttpClient.execute(httpGet);
            //获取响应消息实体
            HttpEntity entity = httpResponse.getEntity();
            //响应状态
            System.out.println("status:" + httpResponse.getStatusLine());
            //判断响应实体是否为空
            if (entity != null) {
                String result = EntityUtils.toString(entity);
                System.out.println("contentEncoding:" + entity.getContentEncoding());
                System.out.println("response content:" + result);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭流并释放资源
                closeableHttpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String httpPost(String url) {
        //HttpClientBuilder
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        //HttpClient
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-type", "application/json; charset=utf-8");
        System.out.println(httpPost.getRequestLine());
        try {
            //ִ
            HttpResponse httpResponse = closeableHttpClient.execute(httpPost);
            //
            HttpEntity entity = httpResponse.getEntity();

            System.out.println("status:" + httpResponse.getStatusLine());
            if (entity != null) {
                String result = EntityUtils.toString(entity, "utf-8");//EntityUtils.toString(entity);
                System.out.println("contentEncoding:" + entity.getContentEncoding());
                System.out.println("response content:" + result);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                closeableHttpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 发送post请求
     *
     * @param url
     * @param paramsMap
     * @return
     */
    public static String httpPostWithMap(String url, Map<String, Object> paramsMap) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom().
                setConnectTimeout(5 * 1000).setConnectionRequestTimeout(5 * 1000)
                .setSocketTimeout(5 * 1000).setRedirectsEnabled(true).build();
        httpPost.setConfig(requestConfig);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        for (String key : paramsMap.keySet()) {
            nvps.add(new BasicNameValuePair(key, String.valueOf(paramsMap.get(key))));
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            logger.info("httpPost ===**********===>>> " + EntityUtils.toString(httpPost.getEntity()));
            HttpResponse response = httpClient.execute(httpPost);
            String strResult = "";
            if (response.getStatusLine().getStatusCode() == 200) {
                strResult = EntityUtils.toString(response.getEntity());
                return strResult;
            } else {
                return "Error Response: " + response.getStatusLine().toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "post failure :caused by-->" + e.getMessage();
        } finally {
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * post json格式数据
     *
     * @param jsonObj
     * @param url
     * @return
     */
    public static String httpPostWithJson(JSONObject jsonObj, String url) {

        HttpPost post = null;
        try {
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setSocketTimeout(5 * 1000)
                    .setConnectTimeout(5 * 1000)
                    .setConnectionRequestTimeout(5 * 1000)
                    .setStaleConnectionCheckEnabled(true)
                    .build();
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(defaultRequestConfig)
                    .build();

            post = new HttpPost(url);
            // 构造消息头
            post.setHeader("Content-type", "application/json; charset=utf-8");
            post.setHeader("Connection", "Close");

            // 构建消息实体
            StringEntity entity = new StringEntity(jsonObj.toString(), Charset.forName("UTF-8"));
            entity.setContentEncoding("UTF-8");
            // 发送Json格式的数据请求
            entity.setContentType("application/json");
            post.setEntity(entity);

            HttpResponse response = httpClient.execute(post);

            // 检验返回码
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entity1 = response.getEntity();
                String result = EntityUtils.toString(entity1, "utf-8");
                System.out.println(result);
                return result;
            } else {
                System.out.println("statusCode:" + statusCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (post != null) {
                post.releaseConnection();
            }
        }
        return null;
    }

    public static String httpPostWithMap(String url, LinkedMultiValueMap<String, Object> paramsMap) {
        RestTemplate client = new RestTemplate();
        ResponseEntity<Object> exchange = null;
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("multipart/form-data");
        headers.setContentType(type);
        org.springframework.http.HttpEntity formEntity = new org.springframework.http.HttpEntity<LinkedMultiValueMap<String, Object>>(paramsMap, headers);
        try {
            logger.info("发送:" + paramsMap.toString());
            exchange = client.exchange(url, HttpMethod.POST, formEntity, Object.class);
            System.out.println("response:" + JSON.toJSONString(exchange.getBody()));
            return JSON.toJSONString(exchange.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }

    public static String getRealIp(HttpServletRequest req) {
        String ip = req.getHeader("X-Real-IP");
        if (ip != null) {
            if (ip.indexOf(',') == -1) {
                return ip;
            }
            return ip.split(",")[0];
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getRemoteAddr();
        }

        return ip;
    }

    public static void main(String[] args) throws JSONException {
        String r = "{\n" +
                "\t\"meeting_id\": 2,\n" +
                "\t\"user_id\": \"27\",\n" +
                "\t\"name\": \"刘能\",\n" +
                "\t\"login_name\": \"刘能2\",\n" +
                "\t\"status\": 1,\n" +
                "\t\"time\": 7845441,\n" +
                "\t\"pic_url\": \"http://....jpg\",\n" +
                "\t\"pic_data\": \"\"\n" +
                "}\n";
        JSONObject sendObj = new JSONObject(r);

        String url = "http://192.168.2.135:18082/meeting/signin/subscription";
        String s = HttpUtil.httpPostWithJson(sendObj, url);
        HttpUtil.httpPostWithJson(sendObj, url);
        JSONObject reObj = new JSONObject(s);
        System.out.println(s);
    }


}
