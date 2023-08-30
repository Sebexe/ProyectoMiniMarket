package principal;

import Ventas.AlmacenVentas;
import Ventas.ProductoVenta;
import Ventas.Venta;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pagos.Credito;
import pagos.Debito;
import pagos.Efectivo;
import productos.Almacen;
import productos.Producto;

import java.io.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;


public class Principal extends Application {
    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.DOWN);


        primaryStage.setMinHeight(800);
        primaryStage.setMinWidth(1200);

        Almacen almacen_principal = cargarAlmacen();
        AlmacenVentas almacen_ventas = cargarAlmacenVentas();
        AtomicReference<Venta> venta_actual = new AtomicReference<>(new Venta());
        ObservableList<Producto> productosObservableList = FXCollections.observableArrayList(almacen_principal.listarProductos());
        ObservableList<ProductoVenta> carritoObservableList = FXCollections.observableArrayList(venta_actual.get().getCarrito());
        ObservableList<Venta> ventasObservableList = FXCollections.observableArrayList(almacen_ventas.recuperarVentas());

        ObservableList<String> options = FXCollections.observableArrayList(
                "Efectivo", "Debito", "Credito 2 cuotas", "Credito 3 cuotas", "Credito 6 cuotas");
        ComboBox<String> comboBox = new ComboBox<>(options);
        comboBox.setValue("Efectivo");
        // Nueva Venta
        HBox producto_form = new HBox();
        TextField venta_codigo = new TextField();venta_codigo.getStyleClass().add("inputs");
        TextField venta_cantidad = new TextField();venta_cantidad.getStyleClass().add("inputs");
        Label controles = new Label("Controles de productos: ");
        controles.getStyleClass().add("texto_venta");
        venta_codigo.setPromptText("Codigo de producto");
        venta_cantidad.setPromptText("Cantidad");
        Label precio_final = new Label();
        precio_final.getStyleClass().add("texto");
        Button boton_agregar_carrito = new Button("Agregar al carrito");
        boton_agregar_carrito.getStyleClass().add("boton_mini");
        boton_agregar_carrito.setOnAction(actionEvent -> {
            int codigo_producto = Integer.parseInt(venta_codigo.getText());
            int cantidad_pedida = Integer.parseInt(venta_cantidad.getText());

            if (almacen_principal.estaProducto(codigo_producto) && almacen_principal.haySuficiente(codigo_producto,cantidad_pedida)) {
                venta_actual.get().agregarCarrito(almacen_principal.buscarProducto(codigo_producto),cantidad_pedida );
                carritoObservableList.setAll(venta_actual.get().getCarrito());
                venta_codigo.clear();
                venta_cantidad.clear();
                precio_final.setText("El precio final es: $" + venta_actual.get().getTotal());
                switch (comboBox.getValue()) {
                    case "Efectivo" ->
                            precio_final.setText("El precio con el descuento en efectivo es de $" + venta_actual.get().getTotal() * 0.9);
                    case "Debito" ->
                            precio_final.setText("El precio final con debito es: $" + venta_actual.get().getTotal());
                    case "Credito 2 cuotas" ->
                            precio_final.setText("Cada una de las 2 cuotas costara $" + df.format((venta_actual.get().getTotal() * 1.06) / 2) + " para un total de $" + df.format(venta_actual.get().getTotal() * 1.06));
                    case "Credito 3 cuotas" ->
                            precio_final.setText("Cada una de las 3 cuotas costara $" + df.format((venta_actual.get().getTotal() * 1.12) / 3) + " para un total de $" + df.format(venta_actual.get().getTotal() * 1.12));
                    case "Credito 6 cuotas" ->
                            precio_final.setText("Cada una de las 6 cuotas costara $" + df.format((venta_actual.get().getTotal() * 1.20) / 6) + " para un total de $" + df.format(venta_actual.get().getTotal() * 1.20));
                }

            }
            else {
                System.out.println("El producto no esta o no hay suficiente stock.");
            }
        });



        producto_form.getChildren().addAll(venta_codigo,venta_cantidad,boton_agregar_carrito);
        producto_form.setSpacing(15);


        TableView<ProductoVenta> producto_tabla = new TableView<>();

        TableColumn<ProductoVenta,Integer> codigo_ventaColumn = new TableColumn<>("Codigo");codigo_ventaColumn.setPrefWidth(100);
        TableColumn<ProductoVenta,String> descripcion_ventaColumn = new TableColumn<>("Descripcion");descripcion_ventaColumn.setPrefWidth(300);
        TableColumn<ProductoVenta,Integer> precio_unitario_ventaColumn = new TableColumn<>("Precio Unitario");precio_unitario_ventaColumn.setPrefWidth(100);
        TableColumn<ProductoVenta,Integer> cantidad_ventaColumn = new TableColumn<>("Cantidad");cantidad_ventaColumn.setPrefWidth(100);
        TableColumn<ProductoVenta,Integer>  total_ventaColumn = new TableColumn<>("Total");total_ventaColumn.setPrefWidth(100);
        codigo_ventaColumn.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        descripcion_ventaColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        precio_unitario_ventaColumn.setCellValueFactory(new PropertyValueFactory<>("precio_unitario"));
        cantidad_ventaColumn.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        total_ventaColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        producto_tabla.setItems(carritoObservableList);
        producto_tabla.getColumns().addAll(codigo_ventaColumn,descripcion_ventaColumn,precio_unitario_ventaColumn,cantidad_ventaColumn,total_ventaColumn);


        VBox producto_box = new VBox(controles,producto_form,producto_tabla,precio_final);
        producto_box.getStyleClass().add("venta_izquierdo");
        producto_box.setStyle("-fx-padding: 0 20px 0 20px;");
        VBox cliente_form = new VBox();
        cliente_form.getStyleClass().add("venta_derecho");
        TextField cuil_cliente = new TextField();
        cuil_cliente.getStyleClass().add("input_cliente");
        cuil_cliente.setPromptText("Cuil Cliente");
        cuil_cliente.setMaxWidth(150);
        Button finalizar_venta = new Button("Finalizar Venta");
        finalizar_venta.getStyleClass().add("boton_mini");
        cliente_form.setSpacing(30);
        cliente_form.setAlignment(Pos.CENTER);
        Label medio_etiqueta = new Label("Medio de pago");
        medio_etiqueta.getStyleClass().add("medio-pago");

        comboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (Objects.equals(newValue, "Efectivo"))
                    precio_final.setText("El precio con el descuento en efectivo es de $" + venta_actual.get().getTotal() * 0.9);
                else if (Objects.equals(newValue, "Debito"))
                    precio_final.setText("El precio final con debito es: $" + venta_actual.get().getTotal());
                else if (Objects.equals(newValue,"Credito 2 cuotas"))
                    precio_final.setText("Cada una de las 2 cuotas costara $" + df.format((venta_actual.get().getTotal()*1.06) / 2) + " para un total de $" + df.format(venta_actual.get().getTotal()*1.06)) ;
                else if (Objects.equals(newValue,"Credito 3 cuotas"))
                    precio_final.setText("Cada una de las 3 cuotas costara $" + df.format((venta_actual.get().getTotal()*1.12) / 3) + " para un total de $" + df.format(venta_actual.get().getTotal()*1.12));
                else if (Objects.equals(newValue,"Credito 6 cuotas"))
                    precio_final.setText("Cada una de las 6 cuotas costara $" + df.format((venta_actual.get().getTotal()*1.20) / 6) + " para un total de $" + df.format(venta_actual.get().getTotal()*1.20));

            }
            });

        cliente_form.getChildren().addAll(cuil_cliente,medio_etiqueta,comboBox, finalizar_venta);


        HBox venta_box = new HBox(producto_box,cliente_form);
        Scene escena_venta = new Scene(venta_box,1200,800);
        producto_box.prefWidthProperty().bind(escena_venta.widthProperty().multiply(0.65));
        producto_tabla.prefWidthProperty().bind(producto_box.widthProperty());
        cliente_form.prefWidthProperty().bind(escena_venta.widthProperty().multiply(0.35));
        cliente_form.prefHeightProperty().bind(escena_venta.heightProperty().multiply(0.70));
        producto_box.setSpacing(30);
        finalizar_venta.setOnAction(actionEvent -> {
            if (!cuil_cliente.getText().isEmpty()){
                int cuil_cliente_venta = Integer.parseInt(cuil_cliente.getText());
                venta_actual.get().setCuil_cliente(cuil_cliente_venta);
                switch (comboBox.getValue()){
                    case "Efectivo":
                        Efectivo pago_actual = new Efectivo(venta_actual.get().getTotal());
                        pago_actual.CalcularCosto();
                        venta_actual.get().setMedio_pago(pago_actual);
                        break;
                    case "Debito":
                        Debito pago_actual_debito = new Debito(venta_actual.get().getTotal());
                        pago_actual_debito.CalcularCosto();
                        venta_actual.get().setMedio_pago(pago_actual_debito);
                        break;
                    case "Credito 2 cuotas":
                        Credito pago_actual_credito2 = new Credito(venta_actual.get().getTotal(),2);
                        pago_actual_credito2.CalcularCosto();
                        venta_actual.get().setMedio_pago(pago_actual_credito2);
                        break;
                    case "Credito 3 cuotas":
                        Credito pago_actual_credito3 = new Credito(venta_actual.get().getTotal(),3);
                        pago_actual_credito3.CalcularCosto();
                        venta_actual.get().setMedio_pago(pago_actual_credito3);
                        break;
                    case "Credito 6 cuotas":
                        Credito pago_actual_credito6 = new Credito(venta_actual.get().getTotal(),6);
                        pago_actual_credito6.CalcularCosto();
                        venta_actual.get().setMedio_pago(pago_actual_credito6);
                        break;
                }

                almacen_ventas.guardarVenta(venta_actual.get());
                guardarAlmacenVentas(almacen_ventas);



                venta_actual.set(new Venta());
                carritoObservableList.setAll(venta_actual.get().getCarrito());
                ventasObservableList.setAll(almacen_ventas.recuperarVentas());
                cuil_cliente.clear();
                precio_final.setText("");
            }
        });


        // Historial de Ventas

        VBox historial = new VBox();
        TableView tabla_historial = new TableView<>();
        tabla_historial.getStyleClass().add("tablita");
        Label historial_texto = new Label("Ultimas ventas");
        historial_texto.getStyleClass().add("texto");
        TableColumn<Venta,Integer> codigo_hisColumn = new TableColumn<>("Codigo");
        TableColumn<Venta,Integer> cuil_cliente_histColumn = new TableColumn<>("Cliente");
        TableColumn<Venta,Integer> total_histColumn = new TableColumn<>("Total");
        TableColumn<Venta,Double> precio_finalColumn = new TableColumn<>("Precio Final");
        TableColumn<Venta,String> metodo_pagohistColumn = new TableColumn<>("Metodo de pago");
        tabla_historial.getColumns().addAll(codigo_hisColumn,cuil_cliente_histColumn,total_histColumn,precio_finalColumn,metodo_pagohistColumn);

        codigo_hisColumn.setCellValueFactory(new PropertyValueFactory<>("numero_venta"));codigo_hisColumn.setPrefWidth(100);
        cuil_cliente_histColumn.setCellValueFactory(new PropertyValueFactory<>("cuil_cliente"));cuil_cliente.setPrefWidth(350);
        total_histColumn.setCellValueFactory(new PropertyValueFactory<>("total"));total_histColumn.setPrefWidth(250);
        precio_finalColumn.setCellValueFactory(new PropertyValueFactory<>("Precio_final"));precio_finalColumn.setPrefWidth(250);
        metodo_pagohistColumn.setCellValueFactory(new PropertyValueFactory<>("Descripcion_metodo"));metodo_pagohistColumn.setPrefWidth(300);

        tabla_historial.setItems(ventasObservableList);


        historial.getChildren().addAll(historial_texto,tabla_historial);
        historial.setAlignment(Pos.TOP_CENTER);
        historial.setSpacing(40);
        historial.getStyleClass().add("formulario_stock_big");
        Scene historia = new Scene(historial,1200,800);





        // Administrar Stock.

        VBox stock_stack = new VBox();
        stock_stack.setSpacing(50);

        TableView<Producto> tabla = new TableView<>();
        tabla.getStyleClass().add("tablita");
        TableColumn<Producto, Integer> codigoColumn = new TableColumn<>("Código");
        TableColumn<Producto, String> descripcionColumn = new TableColumn<>("Descripción");
        TableColumn<Producto, Integer> precioColumn = new TableColumn<>("Precio Unitario");
        TableColumn<Producto, Integer> stockActualColumn = new TableColumn<>("Stock Actual");
        TableColumn<Producto, Integer> stockMinimoColumn = new TableColumn<>("Stock Mínimo");
        TableColumn<Producto, String> estadoColumn = new TableColumn<>("Estado");
        codigoColumn.setCellValueFactory(new PropertyValueFactory<>("codigo"));codigoColumn.setPrefWidth(150);
        descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));descripcionColumn.setPrefWidth(200);
        precioColumn.setCellValueFactory(new PropertyValueFactory<>("precio_unitario"));precioColumn.setPrefWidth(200);
        stockActualColumn.setCellValueFactory(new PropertyValueFactory<>("stock_actual"));stockActualColumn.setPrefWidth(200);
        stockMinimoColumn.setCellValueFactory(new PropertyValueFactory<>("stock_minimo"));stockMinimoColumn.setPrefWidth(200);
        estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));


        tabla.getColumns().addAll(codigoColumn, descripcionColumn, precioColumn, stockActualColumn, stockMinimoColumn,estadoColumn);
        tabla.setItems(productosObservableList);


        HBox formularios_agregar_stock = new HBox();
        formularios_agregar_stock.getStyleClass().add("formulario");
        formularios_agregar_stock.setSpacing(30);

        TextField codigo = new TextField();
        codigo.setPromptText("Codigo de producto");
        codigo.getStyleClass().add("inputs");

        TextField descripcion = new TextField();
        descripcion.setPromptText("Descripcion");
        descripcion.getStyleClass().add("inputs");

        TextField precio_unitario = new TextField();
        precio_unitario.setPromptText("Precio Unitario");
        precio_unitario.getStyleClass().add("inputs");

        TextField stock_actual = new TextField();
        stock_actual.setPromptText("Stock actual");
        stock_actual.getStyleClass().add("inputs");

        TextField stock_minimo = new TextField();
        stock_minimo.setPromptText("Stock minimo");
        stock_minimo.getStyleClass().add("inputs");

        HBox formularios_modificar = new HBox();
        formularios_modificar.getStyleClass().add("formulario");
        formularios_modificar.setSpacing(30);

        TextField codigo_modificar = new TextField();
        codigo_modificar.setPromptText("Codigo de producto");
        codigo_modificar.getStyleClass().add("inputs");

        TextField existencias_agregar = new TextField();
        existencias_agregar.setPromptText("Existencias nuevas");
        existencias_agregar.getStyleClass().add("inputs");

        Button boton_agregar_existencias = new Button("Agregar existencias");
        boton_agregar_existencias.getStyleClass().add("boton_mini");
        boton_agregar_existencias.setOnAction(actionEvent -> {
            int codigo_producto = Integer.parseInt(codigo_modificar.getText());
            if (almacen_principal.estaProducto(codigo_producto)){
                int nuevo_ingreso = Integer.parseInt(existencias_agregar.getText());
                almacen_principal.agregarExistencias(codigo_producto,nuevo_ingreso);
                codigo_modificar.clear();
                existencias_agregar.clear();
                almacen_principal.calcularExistencias(codigo_producto);
                guardarAlmacen(almacen_principal);
                productosObservableList.setAll(almacen_principal.listarProductos());
            }
            else {
                System.out.println("El producto no esta en el almacen.");
            }
        });
        Button boton_sacar_existencias = new Button("Sacar existencias");
        boton_sacar_existencias.getStyleClass().add("boton_mini");
        boton_sacar_existencias.setOnAction(actionEvent -> {
            int codigo_producto = Integer.parseInt(codigo_modificar.getText());
            if (almacen_principal.estaProducto(codigo_producto)){
                int nuevo_ingreso = Integer.parseInt(existencias_agregar.getText());
                almacen_principal.sacarExistencias(codigo_producto,nuevo_ingreso);
                codigo_modificar.clear();
                existencias_agregar.clear();
                almacen_principal.calcularExistencias(codigo_producto);
                guardarAlmacen(almacen_principal);
                productosObservableList.setAll(almacen_principal.listarProductos());
            }
            else {
                System.out.println("El producto no esta en el almacen.");
            }
        });
        Button boton_eliminar_producto = new Button("Eliminar Producto");
        boton_eliminar_producto.getStyleClass().add("boton_mini");
        boton_eliminar_producto.setOnAction(actionEvent -> {
            int codigo_producto = Integer.parseInt(codigo_modificar.getText());
            if (almacen_principal.estaProducto(codigo_producto)){
                almacen_principal.sacarProducto(codigo_producto);
                guardarAlmacen(almacen_principal);
                productosObservableList.setAll(almacen_principal.listarProductos());
            }
            else {
                System.out.println("El producto no esta en el almacen.");
            }
        });
        formularios_agregar_stock.setSpacing(5);
        formularios_modificar.setSpacing(5);
        formularios_modificar.getChildren().addAll(codigo_modificar,existencias_agregar,boton_agregar_existencias,boton_sacar_existencias,boton_eliminar_producto);
        formularios_modificar.getStyleClass().add("formulario_stock");


        Button boton_agregar = new Button("Agregar Producto");
        boton_agregar.getStyleClass().add("boton_mini");
        boton_agregar.setOnAction(actionEvent -> {
            almacen_principal.agregarProducto(Integer.parseInt(codigo.getText()),descripcion.getText(),Integer.parseInt(precio_unitario.getText()),Integer.parseInt(stock_actual.getText()),Integer.parseInt(stock_minimo.getText()));
            almacen_principal.calcularExistencias(Integer.parseInt(codigo.getText()));
            codigo.clear();
            descripcion.clear();
            precio_unitario.clear();
            stock_actual.clear();
            stock_minimo.clear();
            guardarAlmacen(almacen_principal);
            productosObservableList.setAll(almacen_principal.listarProductos());
        });
        Button boton_imprimir = new Button("Volver al Menu");


        formularios_agregar_stock.getChildren().addAll(codigo,descripcion,precio_unitario,stock_actual,stock_minimo,boton_agregar,boton_imprimir);
        formularios_agregar_stock.getStyleClass().add("formulario_stock");
        VBox formularios = new VBox(formularios_agregar_stock,formularios_modificar);

        stock_stack.getChildren().addAll(formularios,tabla);
        stock_stack.getStyleClass().add("formulario_stock_big");
        Scene stock_scene = new Scene(stock_stack,1200,800);



        // MENU

        HBox hbox = new HBox();

        StackPane panel1 = new StackPane();
        panel1.getStyleClass().add("panel1");
        panel1.prefWidthProperty().bind(primaryStage.widthProperty().multiply(0.40));
        panel1.prefHeightProperty().bind(primaryStage.heightProperty());

        StackPane panel2 = new StackPane();
        panel2.getStyleClass().add("panel2");
        panel2.prefWidthProperty().bind(primaryStage.widthProperty().multiply(0.60));
        panel2.prefHeightProperty().bind(primaryStage.heightProperty());

        VBox botones = new VBox();
        botones.setSpacing(90);

        Button boton_venta = new Button("Nueva venta");
        boton_venta.getStyleClass().add("boton");
        boton_venta.prefWidthProperty().bind(panel2.widthProperty().multiply(0.5));
        boton_venta.prefHeightProperty().bind(panel2.heightProperty().multiply(0.15));
        boton_venta.setOnAction(actionEvent -> {
            primaryStage.setScene(escena_venta);
        });


        Button boton_stock = new Button("Administrar Stock");
        boton_stock.getStyleClass().add("boton");
        boton_stock.prefWidthProperty().bind(panel2.widthProperty().multiply(0.5));
        boton_stock.prefHeightProperty().bind(panel2.heightProperty().multiply(0.15));
        boton_stock.setOnAction(actionEvent -> {
            primaryStage.setScene(stock_scene);
        });

        Button boton_historial = new Button("Ver historial");
        boton_historial.getStyleClass().add("boton");
        boton_historial.prefWidthProperty().bind(panel2.widthProperty().multiply(0.5));
        boton_historial.prefHeightProperty().bind(panel2.heightProperty().multiply(0.15));
        boton_historial.setOnAction(actionEvent -> {
            primaryStage.setScene(historia);
        });

        botones.getChildren().addAll(boton_venta,boton_stock,boton_historial);
        botones.setAlignment(Pos.CENTER);
        panel2.getChildren().add(botones);


        hbox.getChildren().addAll(panel1, panel2);

        Scene scene = new Scene(hbox, 1200, 800);
        scene.getStylesheets().add("stylesheet.css");
        escena_venta.getStylesheets().add("stylesheet.css");
        stock_scene.getStylesheets().add("stylesheet.css");
        historia.getStylesheets().add("stylesheet.css");

        primaryStage.setScene(scene);
        primaryStage.setTitle("Administrador");
        primaryStage.show();

        boton_imprimir.setOnAction(actionEvent -> {
            primaryStage.setScene(scene);
        });
        Button boton_menu = new Button("Volver al menu");
        boton_menu.setOnAction(actionEvent -> {
            primaryStage.setScene(scene);
        });

        Button boton_menu2 = new Button("Volver al menu");
        boton_menu2.setOnAction(actionEvent -> {
            primaryStage.setScene(scene);
        });
        boton_imprimir.getStyleClass().add("boton_mini");
        boton_menu.getStyleClass().add("boton_mini");
        boton_menu2.getStyleClass().add("boton_mini");
        historial.getChildren().add(boton_menu2);

        producto_form.getChildren().add(boton_menu);



    }

    private Almacen cargarAlmacen() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("datos.dat"))) {
            return (Almacen) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new Almacen(); // Si ocurre un error, devuelve una lista vacía
        }
    }

    private void guardarAlmacen(Almacen almacen) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("datos.dat"))) {
            outputStream.writeObject(almacen);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private AlmacenVentas cargarAlmacenVentas() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("ventas.dat"))) {
            return (AlmacenVentas) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new AlmacenVentas(); // Si ocurre un error, devuelve una lista vacía
        }
    }

    private void guardarAlmacenVentas(AlmacenVentas almacen) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("ventas.dat"))) {
            outputStream.writeObject(almacen);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
