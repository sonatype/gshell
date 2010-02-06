/*
 * Copyright (C) 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sonatype.gshell.command.resolver;

import org.junit.Test;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link NodePath}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class NodePathTest
{
    @Test
    public void testParent1() {
        NodePath path = new NodePath("/foo/bar/baz");
        assertEquals("/foo/bar", path.parent().toString());
    }

    @Test
    public void testParent2() {
        NodePath path = new NodePath("foo/bar/baz");
        assertEquals("foo/bar", path.parent().toString());
    }

//    @Test
//    public void testParent3() {
//        NodePath path = new NodePath("foo/bar/baz/");
//        assertEquals("foo/bar", path.base().toString());
//    }
    
    private void assertNormalized(final String path, final String expected) {
        assertEquals(expected, new NodePath(path).normalize().toString());
    }
    
    @Test
    public void testNormalize1() {
        assertNormalized("foo/bar/baz", "foo/bar/baz");
    }

    @Test
    public void testNormalize2() {
        assertNormalized("foo/bar/../baz", "foo/baz");
    }

    @Test
    public void testNormalize3() {
        assertNormalized("foo/bar/../../baz", "baz");
    }

    @Test
    public void testNormalize4() {
        assertNormalized("/foo/bar/../../baz", "/baz");
    }

    @Test
    public void testNormalize5() {
        assertNormalized("../foo/bar/../../baz", "../baz");
    }

    @Test
    public void testNormalize6() {
        assertNormalized("/../foo/bar/../../baz", "/../baz");
    }

    @Test
    public void testNormalize7() {
        assertNormalized("../../../../../../foo/bar/../../baz", "../../../../../../baz");
    }

    @Test
    public void testNormalize8() {
        assertNormalized("/../../../../../../foo/bar/../../baz", "/../../../../../../baz");
    }

    @Test
    public void testNormalize9() {
        assertNormalized("foo/////bar", "foo/bar");
    }

    @Test
    public void testNormalize10() {
        assertNormalized(".", ".");
    }

    @Test
    public void testNormalize11() {
        assertNormalized("./foo", "./foo");
    }

    @Test
    public void testNormalize12() {
        assertNormalized("./foo/././.", "./foo/");
    }

    @Test
    public void testNormalize13() {
        assertNormalized("./././././foo/././.", "./foo/");
    }

    @Test
    public void testSplit1() {
        String[] elements = new NodePath("/").split();
        assertNotNull(elements);
        assertEquals(1, elements.length);
        assertEquals("/", elements[0]);
    }

    @Test
    public void testSplit2() {
        String[] elements = new NodePath("foo").split();
        assertNotNull(elements);
        assertEquals(1, elements.length);
        assertEquals("foo", elements[0]);
    }

    @Test
    public void testSplit3() {
        String[] elements = new NodePath("foo/bar").split();
        assertNotNull(elements);
        assertEquals(2, elements.length);
        assertEquals("foo", elements[0]);
        assertEquals("bar", elements[1]);
    }

    @Test
    public void testSplit4() {
        String[] elements = new NodePath("/foo/bar").split();
        assertNotNull(elements);
        assertEquals(3, elements.length);
        assertEquals("/", elements[0]);
        assertEquals("foo", elements[1]);
        assertEquals("bar", elements[2]);
    }
}