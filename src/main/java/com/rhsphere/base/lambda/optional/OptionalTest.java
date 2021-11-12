package com.rhsphere.base.lambda.optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Optional;
import java.util.OptionalDouble;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;


/**
 * @description:
 * @author: ludepeng
 * @date: 2020-12-02 11:22
 */
@Slf4j
public class OptionalTest {

    @Test(expected = IllegalArgumentException.class)
    public void optional() {
        log.info("test");
        assertThat(Optional.of(1).get(), is(1));

        assertThat(Optional.ofNullable(null).orElse("A"), is("A"));

        assertFalse(OptionalDouble.empty().isPresent());

        assertThat(Optional.of(1).map(Math::incrementExact).get(), is(2));

        assertThat(Optional.of(1).filter(i -> (i % 2 ) == 0).orElse(null), is(nullValue()));

        Optional.empty().orElseThrow(IllegalArgumentException::new);


    }

    @Test
    public void opt() {
        Person person = getPerson();

        Optional<Person> personOpt = Optional.ofNullable(person);
        personOpt.ifPresent(System.out::println);

        System.out.println(personOpt.orElse(new Person("无名", 18)));

        System.out.println(personOpt.orElseGet(this::fetchPerson));

        System.out.println(personOpt.map(Person::getName)
            .map(String::toUpperCase)
            .orElse(null));
    }


    private Person getPerson() {
        return null;
    }

    private Person fetchPerson() {
        return new Person("default", 25);
    }

    @Data
    @ToString
    @AllArgsConstructor
    static
    class Person {
        String name;
        int age;
    }
}
