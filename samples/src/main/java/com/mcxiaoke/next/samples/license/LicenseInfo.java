package com.mcxiaoke.next.samples.license;

/**
 * User: mcxiaoke
 * Date: 14-5-27
 * Time: 16:16
 */
public class LicenseInfo {

    public String name;
    public String url;
    public String copyright;
    public String license;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LicenseInfo{");
        sb.append("name='").append(name).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", copyright='").append(copyright).append('\'');
        sb.append(", license='").append(license).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
