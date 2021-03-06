package at.favre.lib.crypto.bcrypt;

import at.favre.lib.bytes.Bytes;
import at.favre.lib.crypto.bcrypt.misc.Repeat;
import at.favre.lib.crypto.bcrypt.misc.RepeatRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertArrayEquals;

public class Radix64Test {

    @Rule
    public RepeatRule repeatRule = new RepeatRule();
    private Radix64Encoder encoder;

    private TestCase[] referenceRadix64Table = new TestCase[]{
            new TestCase("Gu", new byte[]{0x23}),
            new TestCase("u2O", new byte[]{(byte) 0xC3, (byte) 0x84}),
            new TestCase("9txf", new byte[]{(byte) 0xFE, (byte) 0xFC, (byte) 0xE1}),
            new TestCase("hNxTH.", new byte[]{(byte) 0x8C, (byte) 0xFC, (byte) 0xD5, 0x24}),
            new TestCase("id8RFNq", new byte[]{(byte) 0x91, (byte) 0xFF, (byte) 0x93, 0x1C, (byte) 0xFB}),
            new TestCase("wQVuTsH4", new byte[]{(byte) 0xC9, 0x25, (byte) 0xF0, 0x56, (byte) 0xE2, 0x7A}),
            new TestCase("AN1d.KXq4u", new byte[]{0x08, (byte) 0xFD, (byte) 0xDF, 0x00, (byte) 0xC6, 0x6C, (byte) 0xEB}),
            new TestCase("h6BXVr084r2", new byte[]{(byte) 0x8F, (byte) 0xC0, (byte) 0xD9, 0x5E, (byte) 0xDD, (byte) 0xBE, (byte) 0xEA, (byte) 0xDE}),
            new TestCase("0dT6vifCI8aW", new byte[]{(byte) 0xD9, (byte) 0xF5, 0x7C, (byte) 0xC6, 0x48, 0x44, 0x2B, (byte) 0xE7, 0x18}),
            new TestCase("zzfD4GfFebPj6e", new byte[]{(byte) 0xD7, 0x58, 0x45, (byte) 0xE8, (byte) 0x88, 0x47, (byte) 0x81, (byte) 0xD4, 0x65, (byte) 0xF2}),
            new TestCase("P09R9uV5cCDV11K", new byte[]{0x47, 0x6F, (byte) 0xD3, (byte) 0xFF, 0x05, (byte) 0xFB, 0x78, 0x41, 0x57, (byte) 0xDF, 0x73}),
            new TestCase("D7EUIpVIW0l7qkNM", new byte[]{0x17, (byte) 0xD1, (byte) 0x96, 0x2A, (byte) 0xB5, (byte) 0xCA, 0x63, 0x69, (byte) 0xFD, (byte) 0xB2, 0x63, (byte) 0xCE}),
            new TestCase("Vtolm7ldZl8KcHYvcu", new byte[]{0x5E, (byte) 0xFA, (byte) 0xA7, (byte) 0xA3, (byte) 0xD9, (byte) 0xDF, 0x6E, (byte) 0x7F, (byte) 0x8C, 0x78, (byte) 0x96, (byte) 0xB1, 0x7B}),
            new TestCase("87MSgioX97qxvzFfoPG", new byte[]{(byte) 0xFB, (byte) 0xD3, (byte) 0x94, (byte) 0x8A, 0x4A, (byte) 0x99, (byte) 0xFF, (byte) 0xDB, 0x33, (byte) 0xC7, 0x51, (byte) 0xE1, (byte) 0xA9, 0x12}),
            new TestCase("hv442zAAczwYOFY6SHF5", new byte[]{(byte) 0x8F, 0x1E, (byte) 0xBA, (byte) 0xE3, 0x50, (byte) 0x82, 0x7B, 0x5C, (byte) 0x9A, 0x40, 0x76, (byte) 0xBC, 0x50, (byte) 0x91, (byte) 0xFB}),
            new TestCase("5Ao7p22a75KWm6/LcL/uXO", new byte[]{(byte) 0xEC, 0x2A, (byte) 0xBD, (byte) 0xAF, (byte) 0x8E, 0x1C, (byte) 0xF7, (byte) 0xB3, 0x18, (byte) 0xA3, (byte) 0xC0, 0x4D, 0x78, (byte) 0xD0, 0x70, 0x65}),
            new TestCase("K7Mk6gzVXVCZoz4br8mjxVe", new byte[]{0x33, (byte) 0xD3, (byte) 0xA6, (byte) 0xF2, 0x2D, 0x57, 0x65, 0x71, 0x1B, (byte) 0xAB, 0x5E, (byte) 0x9D, (byte) 0xB7, (byte) 0xEA, 0x25, (byte) 0xCD, 0x78}),
            new TestCase("CJHqewYFU0WqKS3fJ0cgLE3Q", new byte[]{0x10, (byte) 0xB2, 0x6C, (byte) 0x83, 0x26, (byte) 0x87, 0x5B, 0x66, 0x2C, 0x31, 0x4E, 0x61, 0x2F, 0x67, (byte) 0xA2, 0x34, 0x6E, 0x52}),
            new TestCase("h0kTxLSkPD4mL1WMyflwMdssje", new byte[]{(byte) 0x8F, 0x69, (byte) 0x95, (byte) 0xCC, (byte) 0xD5, 0x26, 0x44, 0x5E, (byte) 0xA8, 0x37, 0x76, 0x0E, (byte) 0xD2, 0x19, (byte) 0xF2, 0x39, (byte) 0xFB, (byte) 0xAE, (byte) 0x96}),
            new TestCase("LuOXwx4u9ZzV5/t36yMTAslb8Wy", new byte[]{0x37, 0x04, 0x19, (byte) 0xCB, 0x3E, (byte) 0xB0, (byte) 0xFD, (byte) 0xBD, 0x57, (byte) 0xEC, 0x1B, (byte) 0xF9, (byte) 0xF3, 0x43, (byte) 0x95, 0x0A, (byte) 0xE9, (byte) 0xDD, (byte) 0xF9, (byte) 0x8D}),
            new TestCase("mTt2C3/SMa9uLnnfr6WEy3kL163K", new byte[]{(byte) 0xA1, 0x5B, (byte) 0xF8, 0x13, (byte) 0x90, 0x54, 0x39, (byte) 0xCF, (byte) 0xF0, 0x36, (byte) 0x9A, 0x61, (byte) 0xB7, (byte) 0xC6, 0x06, (byte) 0xD3, (byte) 0x99, (byte) 0x8D, (byte) 0xDF, (byte) 0xCE, 0x4C}),
            new TestCase("nnuqg7AQGgp6FBBOj/w5tfPg/xnGtu", new byte[]{(byte) 0xA6, (byte) 0x9C, 0x2C, (byte) 0x8B, (byte) 0xD0, (byte) 0x92, 0x22, 0x2A, (byte) 0xFC, 0x1C, 0x30, (byte) 0xD0, (byte) 0x94, 0x1C, (byte) 0xBB, (byte) 0xBE, 0x14, 0x62, 0x07, 0x3A, 0x48, (byte) 0xBF}),
            new TestCase("PFXsTdmWq6za6S5F1B9D28li7Dlwv4S", new byte[]{0x44, 0x76, 0x6E, 0x55, (byte) 0xFA, 0x18, (byte) 0xB3, (byte) 0xCD, 0x5C, (byte) 0xF1, 0x4E, (byte) 0xC7, (byte) 0xDC, 0x3F, (byte) 0xC5, (byte) 0xE3, (byte) 0xE9, (byte) 0xE4, (byte) 0xF4, 0x59, (byte) 0xF2, (byte) 0xC7, (byte) 0xA5}),
            new TestCase("LusfR3hiP7bGKst1tlckI6S.Ju0rmCIe", new byte[]{0x37, 0x0B, (byte) 0xA1, 0x4F, (byte) 0x98, (byte) 0xE4, 0x47, (byte) 0xD7, 0x48, 0x32, (byte) 0xEB, (byte) 0xF7, (byte) 0xBE, 0x77, (byte) 0xA6, 0x2B, (byte) 0xC5, 0x00, 0x2F, 0x0D, (byte) 0xAD, (byte) 0xA0, 0x42, (byte) 0xA0}),
            new TestCase("ArmQQ/Y7hPv4gii00nXAPe", new byte[]{0x0A, (byte) 0xDA, 0x12, 0x48, 0x16, (byte) 0xBD, (byte) 0x8D, 0x1C, 0x7A, (byte) 0x8A, 0x49, 0x36, (byte) 0xDA, (byte) 0x96, 0x42, 0x46}),
            new TestCase("WNwJW0zlHtW2pawQ76HXV.", new byte[]{0x60, (byte) 0xFC, (byte) 0x8B, 0x63, 0x6D, 0x67, 0x26, (byte) 0xF6, 0x38, (byte) 0xAD, (byte) 0xCC, (byte) 0x92, (byte) 0xF7, (byte) 0xC2, 0x59, 0x5C}),
            new TestCase("Ihd0Lx0pXQquyPZgTFXd5.", new byte[]{0x2A, 0x37, (byte) 0xF6, 0x37, 0x3D, (byte) 0xAB, 0x65, 0x2B, 0x30, (byte) 0xD1, 0x16, (byte) 0xE2, 0x54, 0x76, 0x5F, (byte) 0xEC}),
            new TestCase("sdKbHDooFgr0OOEVXgTKTe", new byte[]{(byte) 0xB9, (byte) 0xF3, 0x1D, 0x24, 0x5A, (byte) 0xAA, 0x1E, 0x2B, 0x76, 0x41, 0x01, (byte) 0x97, 0x66, 0x25, 0x4C, 0x56}),
            new TestCase("gA3GRpUX1hmMUUmyF98Gn.", new byte[]{(byte) 0x88, 0x2E, 0x48, 0x4E, (byte) 0xB5, (byte) 0x99, (byte) 0xDE, 0x3A, 0x0E, 0x59, 0x6A, 0x34, 0x1F, (byte) 0xFF, (byte) 0x88, (byte) 0xA4}),
            new TestCase("SVYk8TP3/CfBLBi/YGiqle", new byte[]{0x51, 0x76, (byte) 0xA6, (byte) 0xF9, 0x54, 0x79, 0x04, 0x48, 0x43, 0x34, 0x39, 0x01, 0x68, (byte) 0x89, 0x2C, (byte) 0x9E}),
            new TestCase("Qk8HfY6w82UI1K8Lf6ZIlO", new byte[]{0x4A, 0x6F, (byte) 0x89, (byte) 0x85, (byte) 0xAF, 0x32, (byte) 0xFB, (byte) 0x85, (byte) 0x8A, (byte) 0xDC, (byte) 0xCF, (byte) 0x8D, (byte) 0x87, (byte) 0xC6, (byte) 0xCA, (byte) 0x9D}),
            new TestCase("5ad9sLQa1ecexexrFsntee", new byte[]{(byte) 0xED, (byte) 0xC7, (byte) 0xFF, (byte) 0xB8, (byte) 0xD4, (byte) 0x9C, (byte) 0xDE, 0x07, (byte) 0xA0, (byte) 0xCE, 0x0C, (byte) 0xED, 0x1E, (byte) 0xEA, 0x6F, (byte) 0x82}),
            new TestCase("sgGn3fI2PiM1ss2HRnlycu", new byte[]{(byte) 0xBA, 0x22, 0x29, (byte) 0xE6, 0x12, (byte) 0xB8, 0x46, 0x43, (byte) 0xB7, (byte) 0xBA, (byte) 0xEE, 0x09, 0x4E, (byte) 0x99, (byte) 0xF4, 0x7B}),
            new TestCase("jsKqqO5SQg.ZDtpq/k5r4O", new byte[]{(byte) 0x96, (byte) 0xE3, 0x2C, (byte) 0xB1, 0x0E, (byte) 0xD4, 0x4A, 0x20, 0x1B, 0x16, (byte) 0xFA, (byte) 0xEC, 0x06, 0x6E, (byte) 0xED, (byte) 0xE9}),
            new TestCase("iL4JMZdQBwXVVXKTfFQRpO", new byte[]{(byte) 0x90, (byte) 0xDE, (byte) 0x8B, 0x39, (byte) 0xB7, (byte) 0xD2, 0x0F, 0x26, 0x57, 0x5D, (byte) 0x93, 0x15, (byte) 0x84, 0x74, (byte) 0x93, (byte) 0xAD}),
            new TestCase("vmSnTF28TuP2KKBAOPbPNu", new byte[]{(byte) 0xC6, (byte) 0x85, 0x29, 0x54, 0x7E, 0x3E, 0x57, 0x04, 0x78, 0x30, (byte) 0xC0, (byte) 0xC2, 0x41, 0x17, 0x51, 0x3F}),
            new TestCase("UwioCLPPqh8EWrHxalX/Qu", new byte[]{0x5B, 0x29, 0x2A, 0x10, (byte) 0xD4, 0x51, (byte) 0xB2, 0x3F, (byte) 0x86, 0x62, (byte) 0xD2, 0x73, 0x72, 0x76, 0x41, 0x4B}),
            new TestCase("Ljfcg2dt2q9mPyk4blNd76/aOFGN8ca", new byte[]{0x36, 0x58, 0x5E, (byte) 0x8B, (byte) 0x87, (byte) 0xEF, (byte) 0xE2, (byte) 0xCF, (byte) 0xE8, 0x47, 0x49, (byte) 0xBA, 0x76, 0x73, (byte) 0xDF, (byte) 0xF7, (byte) 0xC0, 0x5C, 0x40, 0x72, 0x0F, (byte) 0xF9, (byte) 0xE7}),
            new TestCase("4ydWZwAWzZa9YGSf8oEBTOCGY8uBDcO", new byte[]{(byte) 0xEB, 0x47, (byte) 0xD8, 0x6F, 0x20, (byte) 0x98, (byte) 0xD5, (byte) 0xB7, 0x3F, 0x68, (byte) 0x85, 0x21, (byte) 0xFA, (byte) 0xA1, (byte) 0x83, 0x55, 0x01, 0x08, 0x6B, (byte) 0xEC, 0x03, 0x15, (byte) 0xE4}),
            new TestCase("gk3KmMtQnp9Bf3R3z83Qd7WsPcRcPSO", new byte[]{(byte) 0x8A, 0x6E, 0x4C, (byte) 0xA0, (byte) 0xEB, (byte) 0xD2, (byte) 0xA6, (byte) 0xBF, (byte) 0xC3, (byte) 0x87, (byte) 0x94, (byte) 0xF9, (byte) 0xD7, (byte) 0xEE, 0x52, (byte) 0x7F, (byte) 0xD6, 0x2E, 0x45, (byte) 0xE4, (byte) 0xDE, 0x45, 0x44}),
            new TestCase("etOoPrjz6LO5t3FfVB5fP2LGrIPzUBC", new byte[]{(byte) 0x82, (byte) 0xF4, 0x2A, 0x46, (byte) 0xD9, 0x75, (byte) 0xF0, (byte) 0xD4, 0x3B, (byte) 0xBF, (byte) 0x91, (byte) 0xE1, 0x5C, 0x3E, (byte) 0xE1, 0x47, (byte) 0x83, 0x48, (byte) 0xB4, (byte) 0xA4, 0x75, 0x58, 0x31}),
            new TestCase("nztm8zNUXB.UUj3y5rBkrU/ZqHhYDIS", new byte[]{(byte) 0xA7, 0x5B, (byte) 0xE8, (byte) 0xFB, 0x53, (byte) 0xD6, 0x64, 0x30, 0x16, 0x5A, 0x5E, 0x74, (byte) 0xEE, (byte) 0xD0, (byte) 0xE6, (byte) 0xB5, 0x60, 0x5B, (byte) 0xB0, (byte) 0x98, (byte) 0xDA, 0x14, (byte) 0xA5}),
            new TestCase("uxrP8zpLLgWciOUIwobyMvRhEXLiyGS", new byte[]{(byte) 0xC3, 0x3B, 0x51, (byte) 0xFB, 0x5A, (byte) 0xCD, 0x36, 0x26, 0x1E, (byte) 0x91, 0x05, (byte) 0x8A, (byte) 0xCA, (byte) 0xA7, 0x74, 0x3B, 0x14, (byte) 0xE3, 0x19, (byte) 0x93, 0x64, (byte) 0xD0, (byte) 0x85}),
            new TestCase("rs4Ic/w8OunqfpYVu4dn4YoWbvyt6ba", new byte[]{(byte) 0xB6, (byte) 0xEE, (byte) 0x8A, 0x78, 0x1C, (byte) 0xBE, 0x43, 0x0A, 0x6C, (byte) 0x86, (byte) 0xB6, (byte) 0x97, (byte) 0xC3, (byte) 0xA7, (byte) 0xE9, (byte) 0xE9, (byte) 0xAA, (byte) 0x98, 0x77, 0x1D, 0x2F, (byte) 0xF1, (byte) 0xD7}),
            new TestCase("oVrtbl/4uLejWb.wZDhoH3.IhVzHo2K", new byte[]{(byte) 0xA9, 0x7B, 0x6F, 0x76, 0x70, 0x7A, (byte) 0xC0, (byte) 0xD8, 0x25, 0x61, (byte) 0xD0, 0x32, 0x6C, 0x58, (byte) 0xEA, 0x27, (byte) 0x90, 0x0A, (byte) 0x8D, 0x7D, 0x49, (byte) 0xAB, (byte) 0x83}),
            new TestCase("D2iAsq5QhDY3irJSpNuqhiG1MDaJ0C6", new byte[]{0x17, (byte) 0x89, 0x02, (byte) 0xBA, (byte) 0xCE, (byte) 0xD2, (byte) 0x8C, 0x56, (byte) 0xB9, (byte) 0x92, (byte) 0xD2, (byte) 0xD4, (byte) 0xAC, (byte) 0xFC, 0x2C, (byte) 0x8E, 0x42, 0x37, 0x38, 0x57, 0x0B, (byte) 0xD8, 0x4F}),
            new TestCase("1y4csFZglKVmKSXsA6K9suWMzoLA66a", new byte[]{(byte) 0xDF, 0x4E, (byte) 0x9E, (byte) 0xB8, 0x76, (byte) 0xE2, (byte) 0x9C, (byte) 0xC5, (byte) 0xE8, 0x31, 0x46, 0x6E, 0x0B, (byte) 0xC3, 0x3F, (byte) 0xBB, 0x06, 0x0E, (byte) 0xD6, (byte) 0xA3, 0x42, (byte) 0xF3, (byte) 0xC7}),
            new TestCase("9d96OFn4zlo73U69M4eaq/WXqQKcj8C", new byte[]{(byte) 0xFD, (byte) 0xFF, (byte) 0xFC, 0x40, 0x7A, 0x7A, (byte) 0xD6, 0x7A, (byte) 0xBD, (byte) 0xE5, 0x6F, 0x3F, 0x3B, (byte) 0xA8, 0x1C, (byte) 0xB0, 0x16, 0x19, (byte) 0xB1, 0x23, 0x1E, (byte) 0x97, (byte) 0xE1}),
            new TestCase("XP9MELvw..kp0ycxUOlroP7L7Kd0pem", new byte[]{0x65, 0x1F, (byte) 0xCE, 0x18, (byte) 0xDC, 0x72, 0x00, 0x09, (byte) 0xAB, (byte) 0xDB, 0x47, (byte) 0xB3, 0x59, 0x09, (byte) 0xED, (byte) 0xA9, 0x1F, 0x4D, (byte) 0xF4, (byte) 0xC7, (byte) 0xF6, (byte) 0xAE, 0x0A}),
            new TestCase("1ynTIxbFvG/5IXCSzNe1VIFlEEgm9cm", new byte[]{(byte) 0xDF, 0x4A, 0x55, 0x2B, 0x37, 0x47, (byte) 0xC4, (byte) 0x80, 0x7B, 0x29, (byte) 0x91, 0x14, (byte) 0xD4, (byte) 0xF8, 0x37, 0x5C, (byte) 0xA1, (byte) 0xE7, 0x18, 0x68, (byte) 0xA8, (byte) 0xFD, (byte) 0xEA}),
            new TestCase("p2RefvNV3Sg1D4xqLRVCZxnd1LAq.Tq", new byte[]{(byte) 0xAF, (byte) 0x84, (byte) 0xE0, (byte) 0x87, 0x13, (byte) 0xD7, (byte) 0xE5, 0x48, (byte) 0xB7, 0x17, (byte) 0xAC, (byte) 0xEC, 0x35, 0x35, (byte) 0xC4, 0x6F, 0x3A, 0x5F, (byte) 0xDC, (byte) 0xD0, (byte) 0xAC, 0x01, 0x5B}),
            new TestCase("WrcjAiGNm/.ZGAi9Hacv0uPJyFcgplS", new byte[]{0x62, (byte) 0xD7, (byte) 0xA5, 0x0A, 0x42, 0x0F, (byte) 0xA0, 0x10, 0x1B, 0x20, 0x29, 0x3F, 0x25, (byte) 0xC7, (byte) 0xB1, (byte) 0xDB, 0x04, 0x4B, (byte) 0xD0, 0x77, (byte) 0xA2, (byte) 0xAE, 0x75}),
            new TestCase("QwEnfhbWteZue4ywQ1O081lIovkxPUC", new byte[]{0x4B, 0x21, (byte) 0xA9, (byte) 0x86, 0x37, 0x58, (byte) 0xBE, 0x06, (byte) 0xF0, (byte) 0x83, (byte) 0xAD, 0x32, 0x4B, 0x74, 0x36, (byte) 0xFB, 0x79, (byte) 0xCA, (byte) 0xAB, 0x19, (byte) 0xB3, 0x45, 0x61}),
            new TestCase("zG7NQPAtyuGdrsjiC8v3BAvenhdEdJi", new byte[]{(byte) 0xD4, (byte) 0x8F, 0x4F, 0x49, 0x10, (byte) 0xAF, (byte) 0xD3, 0x02, 0x1F, (byte) 0xB6, (byte) 0xE9, 0x64, 0x13, (byte) 0xEC, 0x79, 0x0C, 0x2C, 0x60, (byte) 0xA6, 0x37, (byte) 0xC6, 0x7C, (byte) 0xB9}),
            new TestCase("X5oJYjyuptr0gkCRbE5Kst0JmJ48fLO", new byte[]{0x67, (byte) 0xBA, (byte) 0x8B, 0x6A, 0x5D, 0x30, (byte) 0xAE, (byte) 0xFB, 0x76, (byte) 0x8A, 0x61, 0x13, 0x74, 0x6E, (byte) 0xCC, (byte) 0xBA, (byte) 0xFD, (byte) 0x8B, (byte) 0xA0, (byte) 0xBE, (byte) 0xBE, (byte) 0x84, (byte) 0xD4}),
            new TestCase("0W6X11nLm2q.a8Uj2duJn8jiiswNjyG", new byte[]{(byte) 0xD9, (byte) 0x8F, 0x19, (byte) 0xDF, 0x7A, 0x4D, (byte) 0xA3, (byte) 0x8B, 0x00, 0x73, (byte) 0xE5, (byte) 0xA5, (byte) 0xE1, (byte) 0xFC, 0x0B, (byte) 0xA7, (byte) 0xE9, 0x64, (byte) 0x92, (byte) 0xEC, (byte) 0x8F, (byte) 0x97, 0x42}),
            new TestCase("JbnQ16saEdRAjVd4pQsyt95cJkV3qRW", new byte[]{0x2D, (byte) 0xDA, 0x52, (byte) 0xDF, (byte) 0xCB, (byte) 0x9C, 0x19, (byte) 0xF4, (byte) 0xC2, (byte) 0x95, 0x77, (byte) 0xFA, (byte) 0xAD, 0x2B, (byte) 0xB4, (byte) 0xBF, (byte) 0xFE, (byte) 0xDE, 0x2E, 0x65, (byte) 0xF9, (byte) 0xB1, 0x36})};

    @Before
    public void setUp() {
        encoder = new Radix64Encoder.Default();
    }

    @Test
    @Repeat(3)
    public void testEncodeDifferentLengths() {
        for (int i = 1; i < 128; i++) {
            testSingleEncode(i);
        }
    }

    @Test
    public void testEncode16Bytes() {
        for (int i = 0; i < 256; i++) {
            testSingleEncode(16);
        }
    }

    @Test
    public void testEncode23Bytes() {
        for (int i = 0; i < 256; i++) {
            testSingleEncode(23);
        }
    }

    private void testSingleEncode(int length) {
        byte[] rnd = Bytes.random(length).array();
        byte[] encoded = encoder.encode(rnd);
        byte[] decoded = encoder.decode(encoded);

        assertArrayEquals(rnd, decoded);
        if (length < 1024) {
            System.out.println(Bytes.wrap(encoded).encodeUtf8());
        } else {
            System.out.println(Bytes.wrap(encoded).toString());
        }
        //System.out.println("new EncodeTestCase(\"" + Bytes.wrap(encoded).encodeUtf8() + "\"," + new JavaByteArrayEncoder().encode(rnd) + "),");
    }

    @Test
    public void testEncodeAgainstRefTable() {
        for (TestCase encodeTestCase : referenceRadix64Table) {
            byte[] encoded = encoder.encode(encodeTestCase.raw);
            assertArrayEquals(encodeTestCase.encoded.getBytes(StandardCharsets.UTF_8), encoded);
        }
    }

    @Test
    public void testDecodeAgainstRefTable() {
        for (TestCase encodeTestCase : referenceRadix64Table) {
            byte[] decoded = encoder.decode(encodeTestCase.encoded.getBytes(StandardCharsets.UTF_8));
            assertArrayEquals(encodeTestCase.raw, decoded);
        }
    }

    @Test
    public void testBigBlob() {
        testSingleEncode(1024 * 1024 * 10);
    }

    @Test
    public void testEmptyDecode() {
        assertArrayEquals(new byte[0], encoder.decode(new byte[0]));
    }

    static final class TestCase {
        private final String encoded;
        private final byte[] raw;

        TestCase(String encoded, byte[] raw) {
            this.encoded = encoded;
            this.raw = raw;
        }
    }
}
