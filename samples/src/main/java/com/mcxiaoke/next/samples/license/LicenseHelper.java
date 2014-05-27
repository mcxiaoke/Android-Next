package com.mcxiaoke.next.samples.license;

import android.content.Context;
import com.mcxiaoke.next.utils.IOUtils;
import com.mcxiaoke.next.utils.LogUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * User: mcxiaoke
 * Date: 14-5-27
 * Time: 16:56
 */
public class LicenseHelper {
    public static final String TAG = LicenseHelper.class.getSimpleName();


    public static List<LicenseInfo> parse(Context context, int rawId) {

        final SAXParserFactory factory = SAXParserFactory.newInstance();
        InputStream is = null;
        try {
            final javax.xml.parsers.SAXParser parser = factory.newSAXParser();
            final SAXHandler handler = new SAXHandler();
            is = context.getResources().openRawResource(rawId);
            parser.parse(is, handler);
            return handler.getLicenses();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }
        return null;
    }

    static class SAXHandler extends DefaultHandler {
        public static final String TAG_NOTICES = "notices";
        public static final String TAG_NOTICE = "notice";
        public static final String TAG_NAME = "name";
        public static final String TAG_URL = "url";
        public static final String TAG_COPYRIGHT = "copyright";
        public static final String TAG_LICENSE = "license";

        private List<LicenseInfo> mLicenses;
        private LicenseInfo mInfo;
        private String mCurrentTag;

        public List<LicenseInfo> getLicenses() {
            return mLicenses;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            mLicenses = new ArrayList<LicenseInfo>();
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
        }

        @Override
        public void startElement(final String uri, final String localName,
                                 final String qName, final Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            LogUtils.v(TAG, "startElement() tag=" + localName);
            if (TAG_NOTICE.equals(localName)) {
                mInfo = new LicenseInfo();
            }
            mCurrentTag = localName;
        }

        @Override
        public void characters(final char[] ch, final int start, final int length) throws SAXException {
            super.characters(ch, start, length);
            final String value = new String(ch, start, length);
            if (mInfo != null && value != null) {
                if (TAG_NAME.equals(mCurrentTag)) {
                    mInfo.name = value;
                } else if (TAG_URL.equals(mCurrentTag)) {
                    mInfo.url = value;
                } else if (TAG_COPYRIGHT.equals(mCurrentTag)) {
                    mInfo.copyright = value;
                } else if (TAG_LICENSE.equals(mCurrentTag)) {
                    mInfo.license = value;
                }
            }
        }

        @Override
        public void endElement(final String uri, final String localName,
                               final String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            LogUtils.v(TAG, "endElement() tag=" + localName);
            if (TAG_NOTICE.equals(localName)) {
                mLicenses.add(mInfo);
                mInfo = null;
            }
            mCurrentTag = null;
        }
    }
}
