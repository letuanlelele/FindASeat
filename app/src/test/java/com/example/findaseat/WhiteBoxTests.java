package com.example.findaseat;

import junit.framework.TestCase;

import org.junit.Test;
import static org.junit.Assert.*;

import java.text.ParseException;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 24)
public class WhiteBoxTests {


    @Test
    public void testConvertTo24HourFormat() {
//        MainActivity ma = new MainActivity();
        BuildingPage bp = new BuildingPage();
        int [] arr = {2};
        try {
            assertEquals(2, bp.convertTo24HourFormat("10:10 PM"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}