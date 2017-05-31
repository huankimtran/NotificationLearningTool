package hba.testing.notificatingspcedlearning.NetworkHandler.DataHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;

import hba.testing.notificatingspcedlearning.NetworkHandler.NetworkConstant;
import hba.testing.notificatingspcedlearning.UImanager.OnlineSearchResultListAdapter;

/**
 * Created by TranKim on 2/23/2017.
 */
public class DictionaryDataPuller {
    private static final String BLOCKLEVEL="br BR p P div DIV";
    /**
     * Class for parsing OxFordEnglish website
     */
    public static class OxFordEnglishWebConstant{
        public static final String DATA_WRAPPER ="entryWrapper";
        public static final String[] DATA_TAG ={"gramb","etymology etym","pronSection etym"};
        public static final String[] NOT_USEFUL_DATA_TAG_2={"Origin"};
    }

    /**
     * Class for parsing OALD website
     */
    public static class OALDUnitility {

        public static final int TIME_OUT = 5000;
        public static final String WEBSITE = "http://www.oxfordlearnersdictionaries.com/definition/english/";
        public static final String DATA_WRAPPER = "entryContent";
        public static final String DATA_SELECTOR = "*[id^='<id>']";
        public static final String ENTRY_NAME_SELECTOR = "div[class='webtop-g']";
        public static final String ENTRY_GROUP_SELECTOR = "span[class='pos']";
        public static final String KEY_MEANING="sym_first";
        public static final int KEY_EMOJI_CODE=0x1F511;
        public static ArrayList<Entry> doASearch(String keyword){
            String formatedKeyword=simplifyKeyword(keyword);
            return parseData(validPage(formatedKeyword),formatedKeyword);
        }
        public static String simplifyKeyword(String keyword){
            String re=keyword.trim();
            while((re=re.replace("  "," ")).contains("  "));
            return keyword;
        }
        public static Elements validPage(String keyword) {
            String URIpath = keyword.replaceAll(" ", "-");
            String path = WEBSITE + URIpath;
            Document doc;
            Element data;
            Elements re = new Elements();
            try {
                for (int i = 1; true; i++) {
                    path = WEBSITE + URIpath + "_" + i;
                    doc = Jsoup.parse(new URL(path), TIME_OUT);
                    if ((data = doc.getElementById(DATA_WRAPPER)) != null)
                        re.add(data);
                }
            } catch (Exception e) {
                e.getMessage();
            }
            return re;
        }

        public static ArrayList<Entry> parseData(Elements list, String suffix) {
            suffix = suffix.replaceAll(" ", "-");
            ArrayList<Entry> re=new ArrayList<>();
            for (Element e : list) {
                String entryName="";//=e.select(ENTRY_NAME_SELECTOR).get(0).text();
                String group="";//e.select(ENTRY_GROUP_SELECTOR).get(0).text();
                String pro="";
                String selector=DATA_SELECTOR.replace("<id>",suffix);
                Elements subList = e.select(selector);
                    Entry item=new Entry(WEBSITE,"","","","");
                    for (Element subE : subList) {
                    switch (subE.className()) {
                        //Header contains the name of the item
                        case "top-g":
                            Elements s;
                            if((s=subE.getElementsByClass("webtop-g")).isEmpty()){
                                if(subE.getElementsByClass("pv").isEmpty())
                                    group="Idiom";
                                else
                                    group="Phrasal Verb";
                                entryName=subE.child(0).text()+"("+group+")";
                                pro="";
                            }else{
                                Elements f;
                                if((f=subE.getElementsByClass("pron-gs ei-g")).isEmpty())
                                    pro="";
                                else
                                    pro=f.get(0).text();
                                f=subE.getElementsByClass("pos");
                                group=f.isEmpty()?"Unknown":f.get(0).text();
                                entryName=subE.getElementsByClass("h").get(0).text()+"("+group+")";
                            }
                            break;
                        //The holder of one meaning of the item
                        case "sn-g":
                            //Create a new Entry to hold the new meaning
                            //Check if this is a key meaning
                            boolean key=!(subE.getElementsByClass(KEY_MEANING).isEmpty());
                            item=new Entry(WEBSITE,(key?(new StringBuilder()).appendCodePoint(KEY_EMOJI_CODE).toString()+entryName:entryName),"","",group);
                            item.setContent(subE.text()+(pro.isEmpty()?"":System.getProperty("line.separator")+pro));
                            re.add(item);
                            break;
                        //this is in sng-g. This part holds all the example for the current entry
                        case "x-gs":
                            //Separate the meaning from its example
                            if(!item.getHint().isEmpty())   //check if there is an Item that need to separate its example
                                continue;
                            //Separate the example from the content
                            item.setContent(item.getContent().replace(subE.text(), ""));
                            //Print out the examples while retaining the line breaker between them by access to
                            //each single example through each element of the current x-gs span
                            String example=new String();
                            for(int i=0;i<subE.children().size();i++){
                                example+=subE.child(i).text();
                                if(i!=subE.children().size()-1)
                                    example+=System.getProperty("line.separator");
                            }
                            item.setHint(example);
                            break;
                    }
                }
            }
            return re;
        }
    }
}