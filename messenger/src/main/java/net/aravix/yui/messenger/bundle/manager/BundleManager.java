package net.aravix.yui.messenger.bundle.manager;

import com.google.common.collect.Lists;
import com.google.common.reflect.ClassPath;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import net.aravix.yui.messenger.bundle.Bundle;

import java.util.List;

@Getter
@Log4j2
@UtilityClass
public class BundleManager {
    public final static List<Class<? extends Bundle>> BUNDLES = Lists.newLinkedList();

    public void registerBundle(Class<? extends Bundle> packetClass) {
        BUNDLES.add(BUNDLES.size(), packetClass);
    }

    public int getBundleId(Class<? extends Bundle> packetClass) {
        return BUNDLES.indexOf(packetClass);
    }

    public Class<? extends Bundle> getBundle(int packetId) {
        if (packetId > BUNDLES.size() - 1)
            return null;
        else
            return BUNDLES.get(packetId);
    }

    @SneakyThrows
    public static void register() {
        BUNDLES.clear();

        BundleScanner.scanBundles();
    }

    static {
        register();
    }

    public class BundleScanner {
        @SneakyThrows
        public static void scanBundles() {
            var classLoader = ClassLoader.getSystemClassLoader();
            var classPath = ClassPath.from(classLoader);
            var pkg = "net.aravix.yui.messenger.bundle.impl";

            for (var classInfo : classPath.getTopLevelClassesRecursive(pkg)) {
                var clazz = classInfo.load();

                if (Bundle.class.isAssignableFrom(clazz))
                    registerBundle(clazz);

                scanNestedClasses(clazz);
            }
        }

        private static void scanNestedClasses(Class<?> clazz) {
            var nestedClasses = clazz.getDeclaredClasses();
            for (var nestedClass : nestedClasses) {
                if (Bundle.class.isAssignableFrom(nestedClass))
                    registerBundle(nestedClass);

                scanNestedClasses(nestedClass);
            }
        }

        @SuppressWarnings("unchecked")
        private static void registerBundle(Class<?> bundleClass) {
            BundleManager.registerBundle((Class<? extends Bundle>) bundleClass);
        }
    }
}