package mg.itu.prom16.controller;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import jakarta.servlet.*;

import jakarta.servlet.http.*;
import mg.itu.prom16.annotation.Controller;
import mg.itu.prom16.annotation.Get;
import mg.itu.prom16.mapping.Mapping;

public class FrontController extends HttpServlet {

    List<String> listController = new ArrayList<>();
    String controllerPackage;
    HashMap <String, Mapping> map = new HashMap<>();


    @Override
    public void init() throws ServletException {
        super.init();
        controllerPackage = getInitParameter("package");
        scan();
    }


    @Override
    protected void doGet (HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException {
            processRequest(req, res);
    }

    @Override
    protected void doPost (HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException {
            processRequest(req, res);
    }

    @Override
    public String getServletInfo() {
        return "frontServlet";
    }

    protected void processRequest (HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException {
            res.setContentType("text/html; charset=UTF-8");
            try (PrintWriter out = res.getWriter()) {
                StringBuffer URL = req.getRequestURL();
                String[] URLsplitted = URL.toString().split("/");
                String URLtadiavana = URLsplitted[URLsplitted.length - 1];

                if (map.containsKey(URLtadiavana)) {
                    Mapping mapping = map.get(URLtadiavana);
                    Class<?> clazz = Class.forName(mapping.getClassName());
                    Method method = clazz.getMethod(mapping.getMethodName());
                    Object object = clazz.getDeclaredConstructor().newInstance();
                    Object returnValue = method.invoke(object);
                    String stringValue = (String) returnValue;

                    out.println("Retour est " + stringValue);
                    
                } else {
                    out.println("Empty");
                }
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void scan() {
        try {
            String classesPath = getServletContext().getRealPath("/WEB-INF/classes");
            String decodedPath = URLDecoder.decode(classesPath, "UTF-8");
            String packagePath = decodedPath +"\\"+ controllerPackage.replace('.', '\\');
            File packageDirectory = new File(packagePath);
            if (packageDirectory.exists() && packageDirectory.isDirectory()) {
                File[] classFiles = packageDirectory.listFiles((dir, name) -> name.endsWith(".class"));
                if (classFiles != null) {
                    for (File classFile : classFiles) {
                        String className = controllerPackage + '.' + classFile.getName().substring(0, classFile.getName().length() - 6);
                        try {
                            Class<?> classe = Class.forName(className);
                            if (classe.isAnnotationPresent(Controller.class)) {
                                listController.add(classe.getSimpleName());

                                Method[] listMethod = classe.getMethods();
                                for (Method mtds : listMethod) {
                                    if (mtds.isAnnotationPresent(Get.class)) { // verifie si il y a annotation GET
                                        Mapping mapping = new Mapping(className, mtds.getName());
                                        Get annotation = mtds.getAnnotation(Get.class);
                                        String annotationValue = annotation.value();

                                        map.put(annotationValue, mapping);
                                    }
                                }



                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}