package com.china.fortune.reflex;

import java.io.File;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.china.fortune.global.Log;
import com.sun.xml.internal.ws.api.ResourceLoader;

public class ClassLoaderUtils {
	private URLClassLoader loader = null;

	public void close() {
		if (loader != null) {
			try {
				loader.close();
			} catch (Exception e) {
				Log.logClass(e.getMessage());
			}
			loader = null;
		}
	}

	public boolean loadJar(String sJarUrl) {
		boolean rs = false;
		try {
			if (sJarUrl.startsWith("http")) {
				loader = new URLClassLoader(new URL[] { new URL(sJarUrl) });
			} else {
				File file = new File(sJarUrl);
				if (file.exists()) {
					if (file.isFile()) {
						loader = new URLClassLoader(new URL[] { file.toURI().toURL() });
					} else {
						
					}
				}
			}
			rs = true;
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return rs;
	}

	public boolean loadJarFile(String jarPath) {
		boolean rs = false;
		File file = new File(jarPath);
		if (file.exists() && file.isFile()) {
			try {
				loader = new URLClassLoader(new URL[] { file.toURI().toURL() });
				rs = true;
			} catch (Exception e) {
				Log.logClass(e.getMessage());
			}
		}
		return rs;
	}

	public boolean loadJarUrl(String sUrl) {
		boolean rs = false;
		try {
			loader = new URLClassLoader(new URL[] { new URL(sUrl) });
			rs = true;
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return rs;
	}

	public boolean loadJarFiles(ArrayList<String> lsJarPath) {
		boolean rs = false;
		if (lsJarPath != null && lsJarPath.size() > 0) {
			ArrayList<URL> lsJarURL = new ArrayList<URL>(lsJarPath.size());
			try {
				for (String jarPath : lsJarPath) {
					File file = new File(jarPath);
					if (file.exists() && file.isFile()) {
						lsJarURL.add(file.toURI().toURL());
					}
				}
				if (lsJarURL.size() > 0) {
					loader = new URLClassLoader(lsJarURL.toArray(new URL[0]));
					rs = true;
				}
			} catch (Exception e) {
				Log.logClass(e.getMessage());
			}
		}
		return rs;
	}

	public Class<?> loadClass(String className) {
		Class<?> cls = null;
		if (loader != null && className != null) {
			try {
				cls = loader.loadClass(className);
				if (cls != null) {
					cls.newInstance();
				}
			} catch (Exception e) {
				Log.logClass(e.getMessage());
			}
		}
		return cls;
	}

	public Object create(String className) {
		Object obj = null;
		if (loader != null && className != null) {
			try {
				Class<?> cls = loader.loadClass(className);
				if (cls != null) {
					obj = cls.newInstance();
				}
			} catch (Exception e) {
				Log.logClass(e.getMessage());
			}
		}
		return obj;
	}

	public boolean runMain(String className, String[] args) {
		boolean rs = false;
		try {
			Class<?> cls = loader.loadClass(className);
			if (cls != null) {
				Object obj = cls.newInstance();
				if (obj != null) {
					Class<?>[] cla = new Class[1];
					Object[] val = new Object[1];
					cla[0] = String[].class;
					val[0] = args;
					Method mtd = cls.getMethod("main", cla);
					if (mtd != null) {
						mtd.invoke(obj, val);
						rs = true;
					}
				}
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return rs;
	}


    static public ArrayList<Class<?>> getClassNameByJars(ClassLoader loader, String packageName, boolean childPackage) {
//	                 = Thread.currentThread().getContextClassLoader();
        ArrayList<Class<?>> fileNames = new ArrayList<>();
        ResourceLoader rl = new ResourceLoader() {
            @Override
            public URL getResource(String s) throws MalformedURLException {
                return null;
            }
        };

        String packagePath = packageName.replace(".", "/");
        try {
            Enumeration<URL> urls = loader.getResources(packagePath);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    String type = url.getProtocol();
                    if (type.equals("file")) {
//                        fileNames.addAll(getClassNameByFile(url.getPath(), childPackage));
                    } else if (type.equals("jar")) {
                        fileNames.addAll(getClassNameByJars(loader, url.getPath(), childPackage));
                    } else {
                        fileNames.addAll(getClassNameByJars(loader, url.getPath(), childPackage));
                    }
//                    JarFile JarFilear = ((JarURLConnection)url.openConnection()).getJarFile();
//                    Enumeration<JarEntry> entries = JarFilear.entries();//获取文件，需要递归处理文件夹
                    Log.log(url.getPath());
                }
            }
//            fileNames.addAll(getClassNameByJars(loader, packagePath, childPackage));
        } catch (Exception e) {
        }
        return fileNames;
    }

    public static void main(String[] args) {
        getClassNameByJars(Thread.currentThread().getContextClassLoader(), "com.china.fortune", true);
    }

}
