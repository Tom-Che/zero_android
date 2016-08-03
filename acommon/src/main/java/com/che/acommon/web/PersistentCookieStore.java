package com.che.acommon.web;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PersistentCookieStore implements CookieStore {

    private final HashMap<String, ConcurrentHashMap<String, HttpCookie>> cookies;

    public PersistentCookieStore() {
        cookies = new HashMap<String, ConcurrentHashMap<String, HttpCookie>>();
    }

    @Override
    public void add(URI uri, HttpCookie cookie) {
        String name = getCookieToken(uri, cookie);

        // Save cookie into local store, or remove if expired
        if (!cookie.hasExpired()) {
            if (!cookies.containsKey(uri.getHost()))
                cookies.put(uri.getHost(), new ConcurrentHashMap<String, HttpCookie>());
            cookies.get(uri.getHost()).put(name, cookie);
        } else {
            if (cookies.containsKey(uri.toString()))
                cookies.get(uri.getHost()).remove(name);
        }
    }

    protected String getCookieToken(URI uri, HttpCookie cookie) {
        return cookie.getName() + cookie.getDomain();
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        ArrayList<HttpCookie> ret = new ArrayList<HttpCookie>();
        if (cookies.containsKey(uri.getHost()))
            ret.addAll(cookies.get(uri.getHost()).values());
        return ret;
    }

    @Override
    public boolean removeAll() {
        cookies.clear();
        return true;
    }


    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        String name = getCookieToken(uri, cookie);

        if (cookies.containsKey(uri.getHost()) && cookies.get(uri.getHost()).containsKey(name)) {
            cookies.get(uri.getHost()).remove(name);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<HttpCookie> getCookies() {
        ArrayList<HttpCookie> ret = new ArrayList<HttpCookie>();
        for (String key : cookies.keySet())
            ret.addAll(cookies.get(key).values());

        return ret;
    }

    @Override
    public List<URI> getURIs() {
        ArrayList<URI> ret = new ArrayList<URI>();
        for (String key : cookies.keySet())
            try {
                ret.add(new URI(key));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        return ret;
    }

}