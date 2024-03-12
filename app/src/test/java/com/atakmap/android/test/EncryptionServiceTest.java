
package com.atakmap.android.test;


import static org.junit.Assert.assertEquals;

import com.jtbdefense.atak.mandown.services.EncryptionService;

import org.junit.Test;


public class EncryptionServiceTest {
    @Test
    public void encryptionTest() throws Exception {
        byte[] encoded = EncryptionService.encrypt("alamakota".getBytes());
        byte[] decoded = EncryptionService.decrypt(encoded);
        assertEquals("alamakota", new String(decoded));
    }
}
