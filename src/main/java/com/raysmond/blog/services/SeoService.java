package com.raysmond.blog.services;

import com.raysmond.blog.models.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.List;

@Slf4j
@Service
public class SeoService {

    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    @Autowired
    private AppSetting appSetting;


    public String createSitemap(List<Post> posts) {

        String slash = appSetting.getMainUri().trim().endsWith("/") ? "" : "/";

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element root = doc.createElement("urlset");
            root.setAttribute("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");
            doc.appendChild(root);

            for(Post post : posts) {
                Element url = doc.createElement("url");
                //loc
                Element loc = doc.createElement("loc");
                loc.appendChild(doc.createTextNode(String.format("%s%sposts/%s", appSetting.getMainUri().trim(), slash, post.getPermalink() == null || post.getPermalink().isEmpty() ? post.getId() : post.getPermalink())));
                url.appendChild(loc);
                //lastmod
                //yyyy-MM-dd'T'HH:mm:ss
                Element lastMod = doc.createElement("lastmod");
                lastMod.appendChild(doc.createTextNode(dateFormatter.format(post.getUpdatedAt())));
                url.appendChild(lastMod);
                //
                Element changeFreq = doc.createElement("changefreq");
                changeFreq.appendChild(doc.createTextNode("daily"));
                url.appendChild(changeFreq);

                root.appendChild(url);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            StringWriter sw = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();

        } catch (ParserConfigurationException | TransformerException e) {
            log.error("Error", e);
            return null;
        }

    }

}
