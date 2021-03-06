package me.drakeet.support.about.extension;

import android.support.annotation.Keep;

import java.util.List;

import me.drakeet.support.about.Recommended;

/**
 * @author drakeet
 */
@Keep
public class RecommendedResponse {

    public int code;
    public List<Recommended> data;
}
