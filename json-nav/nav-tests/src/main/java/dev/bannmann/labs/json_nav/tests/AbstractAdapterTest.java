package dev.bannmann.labs.json_nav.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.BOOLEAN;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import org.testng.annotations.Test;

import com.github.mizool.core.exception.CodeInconsistencyException;
import com.google.common.io.Resources;
import dev.bannmann.labs.annotations.SuppressWarningsRationale;
import dev.bannmann.labs.json_nav.AnyRef;
import dev.bannmann.labs.json_nav.ArrayRef;
import dev.bannmann.labs.json_nav.MissingElementException;
import dev.bannmann.labs.json_nav.NullRef;
import dev.bannmann.labs.json_nav.NumberRef;
import dev.bannmann.labs.json_nav.ObjectRef;
import dev.bannmann.labs.json_nav.StringRef;
import dev.bannmann.labs.json_nav.TypeMismatchException;
import dev.bannmann.labs.json_nav.UnexpectedElementException;
import dev.bannmann.labs.json_nav.Value;

@SuppressWarnings({ "java:S2950", "java:S1192", "java:S5612" })
@SuppressWarningsRationale(name = "java:S2950", value = "This is not really production code, so assertions are okay.")
@SuppressWarningsRationale(name = "java:S1192",
    value = "Repeated string literals are okay for tests and therefore also for test helper classes like this.")
@SuppressWarningsRationale(name = "java:S5612", value = "assertSoftly() with longer lambda expressions is fine.")
public abstract class AbstractAdapterTest
{
    private static String loadTestSource()
    {
        try
        {
            var url = Resources.getResource(AbstractAdapterTest.class, "test.json");
            return Resources.toString(url, StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            throw new CodeInconsistencyException(e);
        }
    }

    private static final String TEST_SOURCE = loadTestSource();

    protected abstract ObjectRef load(Reader testData);

    private ObjectRef getObjectRef()
    {
        return load(new StringReader(TEST_SOURCE));
    }

    @Test
    public void getRawJson()
    {
        var objectRef = getObjectRef();
        assertThat(objectRef.getRawJson()).isEqualToIgnoringWhitespace(TEST_SOURCE);
    }

    @Test
    public void obtainAny()
    {
        var objectRef = getObjectRef();

        assertSoftly(softly -> {
            assertThat(objectRef.obtainAny("name")).returns(true, AnyRef::isString);

            assertThat(objectRef.obtainAny("piUseful")).returns(true, AnyRef::isNumber);
            assertThat(objectRef.obtainAny("piRoughly")).returns(true, AnyRef::isNumber);
            assertThat(objectRef.obtainAny("rubiksCube")).returns(true, AnyRef::isNumber);
            assertThat(objectRef.obtainAny("brainNeurons")).returns(true, AnyRef::isNumber);
            assertThat(objectRef.obtainAny("count")).returns(true, AnyRef::isNumber);
            assertThat(objectRef.obtainAny("samplingFrequency")).returns(true, AnyRef::isNumber);
            assertThat(objectRef.obtainAny("weekCount")).returns(true, AnyRef::isNumber);

            assertThat(objectRef.obtainAny("nothingness")).returns(true, AnyRef::isNull);

            assertThat(objectRef.obtainAny("enabled")).returns(true, AnyRef::isBoolean);
            assertThat(objectRef.obtainAny("verbose")).returns(true, AnyRef::isBoolean);

            assertThat(objectRef.obtainAny("alpha")).returns(true, AnyRef::isObject);
            assertThat(objectRef.obtainAny("alpha", "beta")).returns(true, AnyRef::isObject);
            assertThat(objectRef.obtainAny("alpha", "beta", "gamma")).returns(true, AnyRef::isObject);

            assertThat(objectRef.obtainAny("planets")).returns(true, AnyRef::isArray);
            assertThat(objectRef.obtainAny("numbers")).returns(true, AnyRef::isArray);
            assertThat(objectRef.obtainAny("silverBullets")).returns(true, AnyRef::isArray);

            assertThatThrownBy(() -> objectRef.obtainAny("missing")).isInstanceOf(MissingElementException.class);
        });
    }

    @Test
    public void obtainString()
    {
        var objectRef = getObjectRef();

        assertSoftly(softly -> {
            softly.assertThat(objectRef.obtainString("name"))
                .returns("Chell", Value::read);

            softly.assertThatThrownBy(() -> objectRef.obtainObject("name"))
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(() -> objectRef.obtainArray(StringRef.class, "name"))
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(() -> objectRef.obtainArrayOfObjects("name"))
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(() -> objectRef.obtainNumber("name"))
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(() -> objectRef.obtainBoolean("name"))
                .isInstanceOf(TypeMismatchException.class);

            softly.assertThat(objectRef.obtainString("alpha", "beta", "gamma", "targetString"))
                .returns("foo", Value::read);

            softly.assertThatThrownBy(() -> objectRef.obtainString("alpha", "beta", "nothingness", "targetString"))
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(() -> objectRef.obtainString("alpha", "beta", "gamma", "missing"))
                .isInstanceOf(MissingElementException.class);
            softly.assertThatThrownBy(() -> objectRef.obtainString("alpha", "beta", "missing", "targetString"))
                .isInstanceOf(MissingElementException.class);
        });
    }

    @Test
    public void obtainNumber()
    {
        var objectRef = getObjectRef();

        assertSoftly(softly -> {
            softly.assertThat(objectRef.obtainNumber("count"))
                .returns(7, NumberRef::readInteger);

            softly.assertThatThrownBy(() -> objectRef.obtainString("count"))
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(() -> objectRef.obtainBoolean("count"))
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(() -> objectRef.obtainObject("count"))
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(() -> objectRef.obtainArray(StringRef.class, "count"))
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(() -> objectRef.obtainArrayOfObjects("count"))
                .isInstanceOf(TypeMismatchException.class);

            softly.assertThat(objectRef.obtainNumber("alpha", "beta", "gamma", "targetNumber"))
                .returns(1337, NumberRef::readInteger);

            softly.assertThatThrownBy(() -> objectRef.obtainNumber("alpha", "beta", "nothingness", "targetNumber"))
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(() -> objectRef.obtainNumber("alpha", "beta", "gamma", "missing"))
                .isInstanceOf(MissingElementException.class);
            softly.assertThatThrownBy(() -> objectRef.obtainNumber("alpha", "beta", "missing", "targetNumber"))
                .isInstanceOf(MissingElementException.class);
        });
    }

    @Test
    public void obtainArrayOfObjects()
    {
        var objectRef = getObjectRef();

        ArrayRef<ObjectRef> planets = objectRef.obtainArrayOfObjects("planets");
        assertSoftly(softly -> {
            softly.assertThat(planets)
                .hasSize(2)
                .isNotEmpty();

            assertThat(planets.stream()
                .map(element -> element.readString("name"))).containsExactly("Earth", "Mars");

            assertThatThrownBy(planets::onlyElement).isInstanceOf(UnexpectedElementException.class);

            softly.assertThatThrownBy(() -> objectRef.obtainString("planets"))
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(() -> objectRef.obtainBoolean("planets"))
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(() -> objectRef.obtainObject("planets"))
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(() -> objectRef.obtainNumber("planets"))
                .isInstanceOf(TypeMismatchException.class);

            softly.assertThat(objectRef.obtainArrayOfObjects("alpha", "beta", "gamma", "targetArray"))
                .hasSize(1);

            softly.assertThatThrownBy(() -> objectRef.obtainNumber("alpha", "beta", "nothingness", "targetArray"))
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(() -> objectRef.obtainNumber("alpha", "beta", "gamma", "missing"))
                .isInstanceOf(MissingElementException.class);
            softly.assertThatThrownBy(() -> objectRef.obtainNumber("alpha", "beta", "missing", "targetArray"))
                .isInstanceOf(MissingElementException.class);

            ArrayRef<StringRef> stringPlanets = objectRef.obtainArray(StringRef.class, "planets");
            softly.assertThatThrownBy(() -> stringPlanets.iterator()
                    .next())
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(() -> stringPlanets.stream()
                    .iterator()
                    .next())
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(() -> stringPlanets.toList()
                    .get(0))
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(stringPlanets::onlyElement)
                .isInstanceOf(TypeMismatchException.class);
        });
    }

    @Test
    public void obtainEmptyArray()
    {
        var objectRef = getObjectRef();

        ArrayRef<NullRef> silverBullets = objectRef.obtainArray(NullRef.class, "silverBullets");
        assertSoftly(softly -> {
            softly.assertThat(silverBullets)
                .hasSize(0)
                .isEmpty();

            assertThat(silverBullets.stream()).isEmpty();

            assertThatThrownBy(silverBullets::onlyElement).isInstanceOf(MissingElementException.class);

            softly.assertThatThrownBy(() -> objectRef.obtainString("silverBullets"))
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(() -> objectRef.obtainBoolean("silverBullets"))
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(() -> objectRef.obtainObject("silverBullets"))
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(() -> objectRef.obtainNumber("silverBullets"))
                .isInstanceOf(TypeMismatchException.class);
        });
    }

    @Test
    public void tryGetString()
    {
        var objectRef = getObjectRef();

        assertSoftly(softly -> {
            softly.assertThat(objectRef.tryGetString("name"))
                .get()
                .returns("Chell", Value::read);

            softly.assertThat(objectRef.tryGetString("missing"))
                .isEmpty();
            softly.assertThat(objectRef.tryGetString("nothingness"))
                .isEmpty();

            softly.assertThatThrownBy(() -> objectRef.tryGetNumber("name"))
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(() -> objectRef.tryGetObject("name"))
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(() -> objectRef.tryGetArray(StringRef.class, "name"))
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(() -> objectRef.tryGetArrayOfObjects("name"))
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(() -> objectRef.tryGetBoolean("name"))
                .isInstanceOf(TypeMismatchException.class);
        });
    }

    @Test
    public void tryReadString()
    {
        var objectRef = getObjectRef();

        assertSoftly(softly -> {
            softly.assertThat(objectRef.tryReadString("name"))
                .get()
                .isEqualTo("Chell");

            softly.assertThat(objectRef.tryReadString("missing"))
                .isEmpty();
            softly.assertThat(objectRef.tryReadString("nothingness"))
                .isEmpty();

            softly.assertThatThrownBy(() -> objectRef.tryReadBoolean("name"))
                .isInstanceOf(TypeMismatchException.class);
        });
    }

    @Test
    public void tryGetBoolean()
    {
        var objectRef = getObjectRef();

        assertSoftly(softly -> {
            softly.assertThat(objectRef.tryGetBoolean("enabled"))
                .get()
                .returns(true, Value::read);
            softly.assertThat(objectRef.tryGetBoolean("verbose"))
                .get()
                .returns(false, Value::read);

            softly.assertThat(objectRef.tryGetString("missing"))
                .isEmpty();
            softly.assertThat(objectRef.tryGetString("nothingness"))
                .isEmpty();

            softly.assertThatThrownBy(() -> objectRef.tryGetString("enabled"))
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(() -> objectRef.tryGetNumber("enabled"))
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(() -> objectRef.tryGetObject("enabled"))
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(() -> objectRef.tryGetArray(StringRef.class, "enabled"))
                .isInstanceOf(TypeMismatchException.class);
            softly.assertThatThrownBy(() -> objectRef.tryGetArrayOfObjects("enabled"))
                .isInstanceOf(TypeMismatchException.class);
        });
    }

    @Test
    public void tryReadBoolean()
    {
        var objectRef = getObjectRef();

        assertSoftly(softly -> {
            softly.assertThat(objectRef.tryReadBoolean("enabled"))
                .get(BOOLEAN)
                .isTrue();
            softly.assertThat(objectRef.tryReadBoolean("verbose"))
                .get(BOOLEAN)
                .isFalse();

            softly.assertThat(objectRef.tryReadBoolean("missing"))
                .isEmpty();
            softly.assertThat(objectRef.tryReadBoolean("nothingness"))
                .isEmpty();

            softly.assertThatThrownBy(() -> objectRef.tryReadString("enabled"))
                .isInstanceOf(TypeMismatchException.class);
        });
    }

    @Test
    public void readInteger()
    {
        var numberRef = getObjectRef().obtainNumber("samplingFrequency");

        assertSoftly(softly -> {
            softly.assertThat(numberRef.readInteger())
                .isEqualTo(44100);

            softly.assertThat(numberRef.readLong())
                .isEqualTo(44100);

            softly.assertThat(numberRef.readDouble())
                .isEqualTo(44100);

            softly.assertThat(numberRef.readBigDecimal())
                .isEqualTo(new BigDecimal("44100"));

            softly.assertThat(numberRef.readBigInteger())
                .isEqualTo(BigInteger.valueOf(44100));

            softly.assertThatThrownBy(numberRef::readShort)
                .isInstanceOf(TypeMismatchException.class);
        });
    }

    @Test
    public void readLong()
    {
        var numberRef = getObjectRef().obtainNumber("brainNeurons");

        assertSoftly(softly -> {
            softly.assertThat(numberRef.readLong())
                .isEqualTo(86000000000L);

            softly.assertThat(numberRef.readBigDecimal())
                .isEqualTo(BigDecimal.valueOf(86000000000L));

            softly.assertThat(numberRef.readBigInteger())
                .isEqualTo(BigInteger.valueOf(86000000000L));

            softly.assertThat(numberRef.readDouble())
                .isEqualTo(86000000000d);

            softly.assertThatThrownBy(numberRef::readInteger)
                .isInstanceOf(TypeMismatchException.class);

            softly.assertThatThrownBy(numberRef::readShort)
                .isInstanceOf(TypeMismatchException.class);
        });
    }

    @Test
    public void readBigInteger()
    {
        var numberRef = getObjectRef().obtainNumber("rubiksCube");

        assertSoftly(softly -> {
            softly.assertThat(numberRef.readBigInteger())
                .isEqualTo(new BigInteger("43252003274489856000"));

            softly.assertThat(numberRef.readBigDecimal())
                .isEqualTo(new BigDecimal("43252003274489856000"));

            softly.assertThatThrownBy(numberRef::readLong)
                .isInstanceOf(TypeMismatchException.class);

            softly.assertThatThrownBy(numberRef::readInteger)
                .isInstanceOf(TypeMismatchException.class);

            softly.assertThatThrownBy(numberRef::readShort)
                .isInstanceOf(TypeMismatchException.class);

            softly.assertThatThrownBy(numberRef::readDouble)
                .isInstanceOf(TypeMismatchException.class);
        });
    }

    @Test
    public void readBigDecimal()
    {
        var numberRef = getObjectRef().obtainNumber("piUseful");

        assertSoftly(softly -> {
            softly.assertThat(numberRef.readBigDecimal())
                .isEqualTo(new BigDecimal("3.14159265358979323846"));

            softly.assertThatThrownBy(numberRef::readLong)
                .isInstanceOf(TypeMismatchException.class);

            softly.assertThatThrownBy(numberRef::readInteger)
                .isInstanceOf(TypeMismatchException.class);

            softly.assertThatThrownBy(numberRef::readShort)
                .isInstanceOf(TypeMismatchException.class);

            softly.assertThatThrownBy(numberRef::readBigInteger)
                .isInstanceOf(TypeMismatchException.class);

            softly.assertThatThrownBy(numberRef::readDouble)
                .isInstanceOf(TypeMismatchException.class);
        });
    }

    @Test
    public void readShort()
    {
        var numberRef = getObjectRef().obtainNumber("weekCount");

        assertSoftly(softly -> {
            softly.assertThat(numberRef.readShort())
                .isEqualTo((short) 20871);

            softly.assertThat(numberRef.readInteger())
                .isEqualTo(20871);

            softly.assertThat(numberRef.readLong())
                .isEqualTo(20871);

            softly.assertThat(numberRef.readDouble())
                .isEqualTo(20871);

            softly.assertThat(numberRef.readBigDecimal())
                .isEqualTo(new BigDecimal("20871"));

            softly.assertThat(numberRef.readBigInteger())
                .isEqualTo(BigInteger.valueOf(20871));
        });
    }

    @Test
    public void readDouble()
    {
        var numberRef = getObjectRef().obtainNumber("piRoughly");

        assertSoftly(softly -> {
            softly.assertThat(numberRef.readDouble())
                .isEqualTo(3.14159);

            softly.assertThat(numberRef.readBigDecimal())
                .isEqualTo(new BigDecimal("3.14159"));

            softly.assertThatThrownBy(numberRef::readInteger)
                .isInstanceOf(TypeMismatchException.class);

            softly.assertThatThrownBy(numberRef::readLong)
                .isInstanceOf(TypeMismatchException.class);

            softly.assertThatThrownBy(numberRef::readShort)
                .isInstanceOf(TypeMismatchException.class);

            softly.assertThatThrownBy(numberRef::readBigInteger)
                .isInstanceOf(TypeMismatchException.class);
        });
    }
}
