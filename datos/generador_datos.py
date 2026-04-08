import random
import os

def generar_datos(n, archivo):
    with open(archivo, 'w') as f:
        for _ in range(n):
            f.write(str(random.randint(10000000, 99999999)) + '\n')

def cargar_datos(archivo):
    datos = []
    with open(archivo, 'r') as f:
        for linea in f:
            datos.append(int(linea.strip()))
    return datos

def main():
    os.makedirs('datos', exist_ok=True)
    
    tamanos = [10000, 100000, 1000000]
    nombres = ['data_10k.txt', 'data_100k.txt', 'data_1m.txt']
    
    for tamano, nombre in zip(tamanos, nombres):
        ruta = os.path.join('datos', nombre)
        if not os.path.exists(ruta):
            print(f'Generando {nombre} ({tamano} elementos)...')
            generar_datos(tamano, ruta)
        else:
            print(f'{nombre} ya existe, omitiendo...')

if __name__ == '__main__':
    main()