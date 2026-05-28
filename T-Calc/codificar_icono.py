# codificar_icono.py
from base64 import b64encode
import sys
# import pyperclip
filename = sys.argv[1]
with open(filename, "rb") as f:
    # pyperclip.copy(b64encode(f.read()).decode("ascii"))
    print(b64encode(f.read()).decode("ascii"))