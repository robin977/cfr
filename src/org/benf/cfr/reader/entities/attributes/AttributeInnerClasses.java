package org.benf.cfr.reader.entities.attributes;

import org.benf.cfr.reader.bytecode.analysis.types.JavaTypeInstance;
import org.benf.cfr.reader.entities.AccessFlag;
import org.benf.cfr.reader.entities.ConstantPool;
import org.benf.cfr.reader.entities.innerclass.InnerClassInfo;
import org.benf.cfr.reader.util.ListFactory;
import org.benf.cfr.reader.util.bytestream.ByteData;
import org.benf.cfr.reader.util.output.Dumper;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: lee
 * Date: 18/04/2011
 * Time: 19:01
 * To change this template use File | Settings | File Templates.
 */
public class AttributeInnerClasses extends Attribute {
    public static final String ATTRIBUTE_NAME = "InnerClasses";

    private static final long OFFSET_OF_ATTRIBUTE_LENGTH = 2;
    private static final long OFFSET_OF_REMAINDER = 6;
    private static final long OFFSET_OF_NUMBER_OF_CLASSES = 6;
    private static final long OFFSET_OF_CLASS_ARRAY = 8;


    private final int length;
    private final List<InnerClassInfo> innerClassInfoList = ListFactory.newList();

    private static JavaTypeInstance getOptClass(int idx, ConstantPool cp) {
        if (idx == 0) {
            return null;
        }
        return cp.getClassEntry(idx).getTypeInstance(cp);
    }

    private static String getOptName(int idx, ConstantPool cp) {
        if (idx == 0) {
            return null;
        }
        return cp.getUTF8Entry(idx).getValue();
    }

    public AttributeInnerClasses(ByteData raw, ConstantPool cp) {
        this.length = raw.getS4At(OFFSET_OF_ATTRIBUTE_LENGTH);
        int numberInnerClasses = raw.getS2At(OFFSET_OF_NUMBER_OF_CLASSES);
        long offset = OFFSET_OF_CLASS_ARRAY;
        for (int x = 0; x < numberInnerClasses; ++x) {
            short innerClassInfoIdx = raw.getS2At(offset);
            offset += 2;
            short outerClassInfoIdx = raw.getS2At(offset);
            offset += 2;
            short innerNameIdx = raw.getS2At(offset);
            offset += 2;
            int innerAccessFlags = raw.getS2At(offset);
            offset += 2;
            innerClassInfoList.add(new InnerClassInfo(
                    getOptClass(innerClassInfoIdx, cp),
                    getOptClass(outerClassInfoIdx, cp),
                    getOptName(innerNameIdx, cp),
                    AccessFlag.build(innerAccessFlags)
            ));
        }
    }

    @Override
    public String getRawName() {
        return ATTRIBUTE_NAME;
    }

    @Override
    public void dump(Dumper d, ConstantPool cp) {
        d.print(ATTRIBUTE_NAME);
    }

    @Override
    public long getRawByteLength() {
        return OFFSET_OF_REMAINDER + length;
    }

    public List<InnerClassInfo> getInnerClassInfoList() {
        return innerClassInfoList;
    }

    @Override
    public String toString() {
        return ATTRIBUTE_NAME;
    }
}