from tkinter import *
import tkmacosx.widgets.button
from re import search, split, compile
from os import path
from sys import executable #Para la compilación

#Ventana General
ventanaDisplay = Tk()
ventanaDisplay.geometry("266x370+300+300")
ventanaDisplay.config(bg="#2e2e2e")
ventanaDisplay.title("T-Calc")
ventanaDisplay.attributes("-topmost", True)
icono = PhotoImage(file=f"{path.dirname(executable)}/../Resources/icon.png") #Path para la compilación en el ejecutable
ventanaDisplay.iconphoto(True, icono)
ventanaDisplay.minsize(width=266, height=370)
ventanaDisplay.maxsize(width=266, height=370)

#Ventana Histótico
ventanaHistórico = Toplevel()
ventanaHistórico.withdraw()
ventanaHistórico.geometry("266x370+566+300")
ventanaHistórico.title("Histórico")
ventanaHistórico.attributes("-topmost", True)
ventanaHistórico.iconphoto(True, icono)
ventanaHistórico.resizable(width=False, height=False)
ventanaHistórico.protocol("WM_DELETE_WINDOW", ventanaHistórico.withdraw)


#Frame de cajas de texto (Labels)
cajas = Frame(ventanaDisplay, height="100", highlightthickness=0, bg="#2e2e2e")
cajas.pack(padx=5, pady=5, fill="x")

#Frame de Botonera
botonera = Frame(ventanaDisplay, height="300", highlightthickness=0, bg="#2e2e2e")
botonera.pack(padx=5, pady=5, fill="x")

#Variables globales que indican si lo último que se presionó fue Igual u Operación (Banderas)
vengoDelIgual = False
vengoDeOperacion = False

númeroIng = ""
histórico = []

def verifIntConPunto(número):
    if str(type(número)) != "<class 'int'>": #Esto es para que no aparezca ".0" si en realidad es un int
                if número.is_integer():
                    return int(número)
    return número


#Función que modifica la caja de entrada y la de resultado en tiempo real, cuando se presiona un digito o el .
def agregarKey(cajaEntrada, cajaResultado, text): 
    global númeroIng
    global vengoDelIgual
    global vengoDeOperacion

    if vengoDelIgual: #Si lo último que se presionó fue el Igual, antes de iniciar una nueva operación, limpio las cajas
        limpiar(cajaEntrada, cajaResultado)
        vengoDelIgual = False

    if cajaResultado["text"] == "0" and cajaEntrada["text"] == "": #Verifica si las cajas están reseteadas
        if text != "0": #Si ingresa 0 con las cajas reseteadas, no hace nada
            if text == ".": #Si ingresa . le agrega 0 delante
                text = "0."
            
            #Ingresa el texto en las cajas y en el número ingresado
            cajaEntrada["text"] = text
            cajaResultado["text"] = "= " + text
            númeroIng = text
        else:
            return
    else: #Si las cajas no está reseteadas
          #Si ingresa algo distinto al . siendo 0 el número ingresado, no hace nada
        if text != "." and númeroIng == "0":
            return
           
        #Si ingresa . y ya hay un punto en el número ingresado, no hace nada 
        if text == ".":
            if númeroIng.count(".") > 0:
                return
            
            if númeroIng == "": #Si ingresa . le agrega 0 delante
                text = "0."
        
        #Ingresa el ttexto en númeroIng y en la caja de entrada. Luego evalúa la operación
        númeroIng = númeroIng + text
        cajaEntrada["text"] = cajaEntrada["text"] + text
        try: #Ui¡tiliza try para controlar la excepción de división por 0
            resultado = verifIntConPunto(eval(cajaEntrada["text"]))
        
            cajaResultado["text"]= "= " + str(round(resultado, 10))
            vengoDeOperacion = False
        except ZeroDivisionError:
            cajaResultado["text"] = "= Error"
            
  
#Función que agrega la operación ingresada
def operacion(cajaEntrada, text):
    global númeroIng
    global vengoDeOperacion
    global vengoDelIgual
        
    if cajaEntrada["text"] == "": #Si se presiona un operación de primeras, agrega un 0 delante
        cajaEntrada["text"] = "0"
    
    if not vengoDeOperacion: #Aquí se verifica que lo último ingresado ha sido un número y no un símbolo
        if text == "%": #En caso de que se ingrese porcentaje, se saca el porcentaje de toda la operación anterior y utiliza
            if search(r"[\+\-\*\/]", cajaEntrada["text"]) != None:
                porcentaje = split(r"[\+\-\*\/]", cajaEntrada["text"])[-1] #Cantidad de porcentaje
                sinPorc = cajaEntrada["text"][0:len(cajaEntrada["text"])-len(porcentaje)] #Operación sin el porcentaje pero con el último símbolo de operación
                últimaOp = sinPorc[-1] #Símbolo última operación
                operación = split(r"[\+\-]", cajaEntrada["text"][0:len(cajaEntrada["text"])-len(porcentaje)-1])[-1] #Operación a la cual sacar el porcentaje
                
                if últimaOp == "+" or últimaOp =="-": #En caso de suma o resta, se agrega o resta el porcentaje de "operación"
                    resultadoPorc = verifIntConPunto(eval(f"{operación} * {porcentaje} / 100"))
                else: #En caso de mult. o div. se obtiene el porcentaje de "operación"
                    resultadoPorc = verifIntConPunto(eval(f"1 / 10"))
                
                cajaEntrada["text"] = f"{sinPorc}{str(resultadoPorc)}"
                cajaResultado["text"] = "= " + str(round(verifIntConPunto(eval(cajaEntrada["text"])), 10))
            else:
                return
            
            vengoDeOperacion = False #Si presiono % hace la cuenta inmediatamente con lo que luego puedo agregar otra operación
        else:       
            cajaEntrada["text"] = cajaEntrada["text"] + text
            númeroIng = ""
            vengoDeOperacion = True   
                
        vengoDelIgual = False


#Función que se ejecuta cuando se presiona el =
def igual(cajaEntrada, cajaResultado):

    global númeroIng
    global histórico    
    global vengoDelIgual
    global vengoDeOperacion
    
    #Una vez dado al igual, se agrega registro al histórico y se igualan las cajas
    if not vengoDelIgual:
        histórico.append(cajaEntrada["text"] + "=" + cajaResultado["text"][2:len(cajaResultado["text"])])
        cajaEntrada["text"] = cajaResultado["text"][2:len(cajaResultado["text"])]
    
        cajaHistórico.insert(END, histórico[len(histórico)-1])
    
    vengoDelIgual = True
    vengoDeOperacion = False 


#Función para limpiar resetear las cajas
def limpiar(cajaEntrada, cajaResultado):
    global númeroIng
    global vengoDelIgual
    global vengoDeOperacion
    
    if cajaEntrada["text"] == "": #Si la caja de entrada no tiene nada, y se presiona C, borra el histórico.
        cajaHistórico.delete(0, END)
        
    cajaEntrada["text"] = ""
    cajaResultado["text"] = "0"
    númeroIng = ""
    vengoDelIgual = False
    vengoDeOperacion = False

    
#Función para borrar de a un caracter
def borrar(cajaEntrada, cajaResultado):
    global númeroIng
    global vengoDelIgual
    global vengoDeOperacion
    
    #Verifica que la caja de entrada tenga 1 caracter o menos, o que lo último presionado es el =
    #En tal caso, directamente resetea las cajas
    if len(cajaEntrada["text"]) < 2 or vengoDelIgual:
        limpiar(cajaEntrada, cajaResultado)
    else:
        #De lo contrario, quita un caracter a la caja de entrada y a númeroIng
        cajaEntrada["text"] = cajaEntrada["text"][0:len(cajaEntrada["text"])-1]
        númeroIng = númeroIng[0:-1]
        #Luego se guarda el último caracter luego del borrado
        caracterAnt = cajaEntrada["text"][-1]
        númeroIng = númeroIng[0:-1]
            
        #En caso de que este último caracter no sea un símbolo, llama a la función agregarKey con un texto vacío
        #para que evalúe nuevamente la operación y actualice la caja de resultado
        if not compile(r"[\+\-\*\/]").match(caracterAnt):
            agregarKey(cajaEntrada, cajaResultado, "")
        else:
            #De lo contrario, crea una caja temporal, sin este último símbolo para reevaluar la operación
            cajaTemporal = Label(text=cajaEntrada["text"][0:len(cajaEntrada["text"])-1])
            agregarKey(cajaTemporal, cajaResultado, "")
            vengoDeOperacion = True
       
       
#Función para manejo de transparencia
def transp(cambio):
    if cambio == "+":
        ventanaDisplay.attributes("-alpha", ventanaDisplay.attributes("-alpha") + 0.1)
        ventanaHistórico.attributes("-alpha", ventanaDisplay.attributes("-alpha") + 0.1)
    elif ventanaDisplay.attributes("-alpha") > 0.3:
        ventanaDisplay.attributes("-alpha", ventanaDisplay.attributes("-alpha") - 0.1)
        ventanaHistórico.attributes("-alpha", ventanaDisplay.attributes("-alpha") - 0.1)


#Función para copiar datos de histórico a cajas
def copy(cajaEntrada, cajaResultado, cajaHistórico):
    if cajaHistórico.size() > 0:
        cálculo = cajaHistórico.selection_get().split("=")[0]
        resultado = cajaHistórico.selection_get().split("=")[1]
        cajaEntrada["text"] = cálculo
        cajaResultado["text"] = "= " + resultado
    
        global vengoDeOperacion
        global vengoDelIgual
        vengoDeOperacion = False
        vengoDelIgual = True

    
#Definición de Cajas de Entrada y Resultado
cajaEntrada = Label(cajas, width=13, border=0, highlightthickness=0, anchor="e", justify="right", font=('Arial 20'), bg="#2e2e2e", fg="white")
cajaResultado = Label(cajas, width=13, border=0, highlightthickness=0, anchor="e", justify="right", font=('Arial 20'), bg="#2e2e2e", fg="white")
cajaResultado["text"] = "0"

#Colocación en la grilla de las cajas
cajaResultado.grid(row=1, column=0, ipadx=41)
cajaEntrada.grid(row=0, column=0, ipadx=41)

#Definición y colocación de la botonera numérica
boton1 = tkmacosx.Button(botonera,text="1", command= lambda: agregarKey(cajaEntrada, cajaResultado, "1"), width=40, height=40, font=('Arial 20'), bg="grey", fg="white", borderwidth=5, bordercolor="grey", cursor="hand", activebackground="grey")
boton2 = tkmacosx.Button(botonera,text="2", command= lambda: agregarKey(cajaEntrada, cajaResultado, "2"), width=40, height=40, font=('Arial 20'), bg="grey", fg="white", borderwidth=5, bordercolor="grey", cursor="hand", activebackground="grey")
boton3 = tkmacosx.Button(botonera,text="3", command= lambda: agregarKey(cajaEntrada, cajaResultado, "3"), width=40, height=40, font=('Arial 20'), bg="grey", fg="white", borderwidth=5, bordercolor="grey", cursor="hand", activebackground="grey")
boton4 = tkmacosx.Button(botonera,text="4", command= lambda: agregarKey(cajaEntrada, cajaResultado, "4"), width=40, height=40, font=('Arial 20'), bg="grey", fg="white", borderwidth=5, bordercolor="grey", cursor="hand", activebackground="grey")
boton5 = tkmacosx.Button(botonera,text="5", command= lambda: agregarKey(cajaEntrada, cajaResultado, "5"), width=40, height=40, font=('Arial 20'), bg="grey", fg="white", borderwidth=5, bordercolor="grey", cursor="hand", activebackground="grey")
boton6 = tkmacosx.Button(botonera,text="6", command= lambda: agregarKey(cajaEntrada, cajaResultado, "6"), width=40, height=40, font=('Arial 20'), bg="grey", fg="white", borderwidth=5, bordercolor="grey", cursor="hand", activebackground="grey")
boton7 = tkmacosx.Button(botonera,text="7", command= lambda: agregarKey(cajaEntrada, cajaResultado, "7"), width=40, height=40, font=('Arial 20'), bg="grey", fg="white", borderwidth=5, bordercolor="grey", cursor="hand", activebackground="grey")
boton8 = tkmacosx.Button(botonera,text="8", command= lambda: agregarKey(cajaEntrada, cajaResultado, "8"), width=40, height=40, font=('Arial 20'), bg="grey", fg="white", borderwidth=5, bordercolor="grey", cursor="hand", activebackground="grey")
boton9 = tkmacosx.Button(botonera,text="9", command= lambda: agregarKey(cajaEntrada, cajaResultado, "9"), width=40, height=40, font=('Arial 20'), bg="grey", fg="white", borderwidth=5, bordercolor="grey", cursor="hand", activebackground="grey")
boton0 = tkmacosx.Button(botonera,text="0", command= lambda: agregarKey(cajaEntrada, cajaResultado, "0"), width=91, height=40, font=('Arial 20'), bg="grey", fg="white", borderwidth=5, bordercolor="grey", cursor="hand", activebackground="grey")
botonComa = tkmacosx.Button(botonera,text=".", command= lambda: agregarKey(cajaEntrada, cajaResultado, "."), width=40, height=40, font=('Arial 20'), bg="grey", fg="white", borderwidth=5, bordercolor="grey", cursor="hand", activebackground="grey")

boton1.grid(row=2, column=0, padx=0)
boton2.grid(row=2, column=1, padx=1)
boton3.grid(row=2, column=2, padx=0)
boton4.grid(row=1, column=0, padx=0)
boton5.grid(row=1, column=1, padx=0, pady=1)
boton6.grid(row=1, column=2, padx=0)
boton7.grid(row=0, column=0, padx=0)
boton8.grid(row=0, column=1, padx=0)
boton9.grid(row=0, column=2, padx=0)
boton0.grid(row=3, column=0, columnspan=2, padx=0, pady=1)
botonComa.grid(row=3, column=2, padx=0)

#Definición y colocación de la botonera operacional
botonMás = tkmacosx.Button(botonera,text="+", command=lambda: operacion(cajaEntrada, "+"), width=40, height=40, font=('Arial 20'), bg="orange", fg="white", borderwidth=5, bordercolor="orange", cursor="hand", activebackground="orange")
botonMenos = tkmacosx.Button(botonera,text="-", command=lambda: operacion(cajaEntrada, "-"), width=40, height=40, font=('Arial 20'), bg="orange", fg="white", borderwidth=5, bordercolor="orange", cursor="hand", activebackground="orange")
botonMult = tkmacosx.Button(botonera,text="x", command=lambda: operacion(cajaEntrada, "*"), width=40, height=40, font=('Arial 20'), bg="orange", fg="white", borderwidth=5, bordercolor="orange", cursor="hand", activebackground="orange")
botonDiv = tkmacosx.Button(botonera,text="/", command=lambda: operacion(cajaEntrada, "/"), width=40, height=40, font=('Arial 20'), bg="orange", fg="white", borderwidth=5, bordercolor="orange", cursor="hand", activebackground="orange")
botonPorc = tkmacosx.Button(botonera,text="%", command=lambda: operacion(cajaEntrada, "%"), width=40, height=40, font=('Arial 20'), bg="orange", fg="white", borderwidth=5, bordercolor="orange", cursor="hand", activebackground="orange")
botonIgual = tkmacosx.Button(botonera,text="=", command=lambda: igual(cajaEntrada, cajaResultado), width=193, height=40, font=('Arial 20'), bg="orange", fg="white", borderwidth=5, bordercolor="orange", cursor="hand", activebackground="orange")

botonMás.grid(row=3, column=3, padx=1)
botonMenos.grid(row=2, column=3)
botonMult.grid(row=1, column=3)
botonDiv.grid(row=0, column=3)
botonPorc.grid(row=2, column=4)
botonIgual.grid(row=4, column=0, columnspan=4)

#Definición y colocación de la botonera de borrado, y transparencia
botonC = tkmacosx.Button(botonera,text="C", command=lambda: limpiar(cajaEntrada, cajaResultado), width=40, height=40, font=('Arial 20'), bg="#84b6f4", fg="grey", borderwidth=5, bordercolor="#84b6f4", cursor="hand", activebackground="#84b6f4")
botonDel = tkmacosx.Button(botonera,text="<", command=lambda: borrar(cajaEntrada, cajaResultado), width=40, height=40, font=('Arial 20'), bg="#84b6f4", fg="grey", borderwidth=5, bordercolor="#84b6f4", cursor="hand", activebackground="#84b6f4")
transpMas = tkmacosx.Button(botonera, text="Transp +", command=lambda: transp("+"), width=40, height=40, font=('Arial 10'), bg="#28B463", fg="black", borderwidth=5, bordercolor="#28B463", cursor="hand", activebackground="#28B463")
transpMenos = tkmacosx.Button(botonera, text="Transp -", command=lambda: transp("-"), width=40, height=40, font=('Arial 10'), bg="#ABEBC6", fg="black", borderwidth=5, bordercolor="#ABEBC6", cursor="hand", activebackground="#ABEBC6")
botonC.grid(row=0, column=4)
botonDel.grid(row=1, column=4)
transpMas.grid(row=3, column=4)
transpMenos.grid(row=4, column=4)

#Definición y colocación del botón histórico
botonHist = tkmacosx.Button(botonera, text="Histórico >", command=lambda: ventanaHistórico.deiconify() if ventanaHistórico.state() == "withdrawn" else ventanaHistórico.withdraw(), width=200, height=30, font=('Arial 20'), borderless=1)
botonHist.grid(row=5, column=0, columnspan=5, pady=5)

#Definición de eventos de la ventana para presionar en el teclado
ventanaDisplay.bind("1", lambda event: agregarKey(cajaEntrada, cajaResultado, "1"))
ventanaDisplay.bind("2", lambda event: agregarKey(cajaEntrada, cajaResultado, "2"))
ventanaDisplay.bind("3", lambda event: agregarKey(cajaEntrada, cajaResultado, "3"))
ventanaDisplay.bind("4", lambda event: agregarKey(cajaEntrada, cajaResultado, "4"))
ventanaDisplay.bind("5", lambda event: agregarKey(cajaEntrada, cajaResultado, "5"))
ventanaDisplay.bind("6", lambda event: agregarKey(cajaEntrada, cajaResultado, "6"))
ventanaDisplay.bind("7", lambda event: agregarKey(cajaEntrada, cajaResultado, "7"))
ventanaDisplay.bind("8", lambda event: agregarKey(cajaEntrada, cajaResultado, "8"))
ventanaDisplay.bind("9", lambda event: agregarKey(cajaEntrada, cajaResultado, "9"))
ventanaDisplay.bind("0", lambda event: agregarKey(cajaEntrada, cajaResultado, "0"))
ventanaDisplay.bind(".", lambda event: agregarKey(cajaEntrada, cajaResultado, "."))

ventanaDisplay.bind("<Return>", lambda event: igual(cajaEntrada, cajaResultado))
ventanaDisplay.bind("+", lambda event: operacion(cajaEntrada, "+"))
ventanaDisplay.bind("-", lambda event: operacion(cajaEntrada, "-"))
ventanaDisplay.bind("*", lambda event: operacion(cajaEntrada, "*"))
ventanaDisplay.bind("/", lambda event: operacion(cajaEntrada, "/"))
ventanaDisplay.bind("%", lambda event: operacion(cajaEntrada, "%"))
ventanaDisplay.bind("<BackSpace>", lambda event: borrar(cajaEntrada, cajaResultado))
ventanaDisplay.bind("<Escape>", lambda event: limpiar(cajaEntrada, cajaResultado))
ventanaDisplay.bind("<Up>", lambda event: transp("+"))
ventanaDisplay.bind("<Down>", lambda event: transp("-"))

#Menejo de transparencia con el ratón
ventanaDisplay.bind("<MouseWheel>", lambda event: transp("+") if event.delta > 0 else transp("-"))
ventanaHistórico.bind("<MouseWheel>", lambda event: transp("+") if event.delta > 0 else transp("-"))

#Definición de la caja de Histórico con su selección de ratón
cajaHistórico = Listbox(ventanaHistórico, font=('Arial 20'), justify="right", bg="#2e2e2e", fg="white", highlightthickness=0)
cajaHistórico.place(relwidth=1, relheight=1)
cajaHistórico.bind("<<ListboxSelect>>", lambda event: copy(cajaEntrada, cajaResultado, cajaHistórico))

ventanaDisplay.mainloop()