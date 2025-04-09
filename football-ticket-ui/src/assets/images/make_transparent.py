import cv2
import numpy as np

# Load the image
image = cv2.imread('real_barce_739x550.png')  # Replace with your image file path

# Convert BGR to BGRA (add alpha channel)
image_bgra = cv2.cvtColor(image, cv2.COLOR_BGR2BGRA)

# Define the range of red colors to make transparent (adjust these values as needed)
# In BGR, red is [0, 0, 255], but the background might have variations
lower_red = np.array([0, 0, 200])  # Lower bound for red (BGR)
upper_red = np.array([50, 50, 255])  # Upper bound for red (BGR)

# Create a mask for the red background
mask = cv2.inRange(image, lower_red, upper_red)

# Set the alpha channel to 0 where the mask is True (transparent)
image_bgra[mask == 255, 3] = 0

# Save the result as a PNG (PNG supports transparency, JPG doesn’t)
cv2.imwrite('real_barce_v1_739x550.png', image_bgra)
