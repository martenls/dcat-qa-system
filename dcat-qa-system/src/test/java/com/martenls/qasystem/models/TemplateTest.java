package com.martenls.qasystem.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TemplateTest {


    @Test
    void countTemplate() {
        Template template = new Template("SELECT (COUNT(DISTINCT ?var0) AS ?count)\n" +
                "WHERE {\n" +
                "    ?var0 <prop0> <entity0>\n" +
                "}");
        assertTrue(template.hasCountAggregate());
        assertTrue(template.hasDistinctModifier());
        assertFalse(template.hasIntervalFilter());
        assertFalse(template.hasGroupByAggregate());
        assertFalse(template.hasOrderAscModifier());
        assertFalse(template.hasOrderDescModifier());
        assertFalse(template.hasHavingAggregate());
        assertFalse(template.hasLimitModifier());
        assertFalse(template.hasLiteralArrayFilter());
        assertFalse(template.hasLiteralFilter());
        assertEquals(1, template.getPropertyCount());
        assertEquals(1, template.getEntityCount());
        assertEquals(0, template.getLiteralArrayCount());
        assertEquals(0, template.getLiteralCount());
    }

    @Test
    void stringFilterTemplate() {
        Template template = new Template("SELECT (COUNT(DISTINCT ?var0) AS ?count)\n" +
                "WHERE {\n" +
                "    ?var0 <prop0> ?var1.\n" +
                "    ?var1 <prop1> ?var2\n" +
                "    FILTER ( ?var2 IN (<literalArray0>) )\n" +
                "}");
        assertTrue(template.hasCountAggregate());
        assertTrue(template.hasDistinctModifier());
        assertFalse(template.hasIntervalFilter());
        assertFalse(template.hasGroupByAggregate());
        assertFalse(template.hasOrderAscModifier());
        assertFalse(template.hasOrderDescModifier());
        assertFalse(template.hasHavingAggregate());
        assertFalse(template.hasLimitModifier());
        assertTrue(template.hasLiteralArrayFilter());
        assertFalse(template.hasLiteralFilter());
        assertEquals(2, template.getPropertyCount());
        assertEquals(0, template.getEntityCount());
        assertEquals(1, template.getLiteralArrayCount());
        assertEquals(0, template.getLiteralCount());
    }

    @Test
    void countGroupOrderTemplate() {
        Template template = new Template("SELECT ?var0 (COUNT(?var1) AS ?count)\n" +
                "WHERE {\n" +
                "     ?var1 <prop0> ?var0 ;\n" +
                "     <prop1> <entity0> \n" +
                "}\n" +
                "GROUP BY ?var0\n" +
                "ORDER BY DESC(?count)\n" +
                "LIMIT 1");
        assertTrue(template.hasCountAggregate());
        assertFalse(template.hasDistinctModifier());
        assertFalse(template.hasIntervalFilter());
        assertTrue(template.hasGroupByAggregate());
        assertFalse(template.hasOrderAscModifier());
        assertTrue(template.hasOrderDescModifier());
        assertFalse(template.hasHavingAggregate());
        assertTrue(template.hasLimitModifier());
        assertFalse(template.hasLiteralArrayFilter());
        assertFalse(template.hasLiteralFilter());
        assertEquals(2, template.getPropertyCount());
        assertEquals(1, template.getEntityCount());
        assertEquals(0, template.getLiteralArrayCount());
        assertEquals(0, template.getLiteralCount());
    }

    @Test
    void intervalTemplate() {
        Template template = new Template("SELECT ?var0\n" +
                "WHERE {\n" +
                "    ?var0 <prop0> ?var1.\n" +
                "    ?var1 <prop1> <entity0>.\n" +
                "    ?var1 <prop2> <var2>.\n" +
                "    FILTER ( ?var2 >= <lbound0> && ?var2 <= <rbound0>)\n" +
                "}");
        assertFalse(template.hasCountAggregate());
        assertFalse(template.hasDistinctModifier());
        assertTrue(template.hasIntervalFilter());
        assertFalse(template.hasGroupByAggregate());
        assertFalse(template.hasOrderAscModifier());
        assertFalse(template.hasOrderDescModifier());
        assertFalse(template.hasHavingAggregate());
        assertFalse(template.hasLimitModifier());
        assertFalse(template.hasLiteralArrayFilter());
        assertFalse(template.hasLiteralFilter());
        assertEquals(3, template.getPropertyCount());
        assertEquals(1, template.getEntityCount());
        assertEquals(0, template.getLiteralArrayCount());
        assertEquals(0, template.getLiteralCount());
    }

    @Test
    void valueFilterTemplate() {
        Template template = new Template("SELECT ?var0\n" +
                "WHERE {\n" +
                "    ?var0 <prop0> ?var1.\n" +
                "    ?var0 <prop1> <entity0>.\n" +
                "    FILTER ( ?var1 = <literal0> )\n" +
                "}");
        assertFalse(template.hasCountAggregate());
        assertFalse(template.hasDistinctModifier());
        assertFalse(template.hasIntervalFilter());
        assertFalse(template.hasGroupByAggregate());
        assertFalse(template.hasOrderAscModifier());
        assertFalse(template.hasOrderDescModifier());
        assertFalse(template.hasHavingAggregate());
        assertFalse(template.hasLimitModifier());
        assertFalse(template.hasLiteralArrayFilter());
        assertTrue(template.hasLiteralFilter());
        assertEquals(2, template.getPropertyCount());
        assertEquals(1, template.getEntityCount());
        assertEquals(0, template.getLiteralArrayCount());
        assertEquals(1, template.getLiteralCount());
    }

}