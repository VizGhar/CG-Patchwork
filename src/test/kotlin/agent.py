import sys
import math

# Become the master of patchworking by filling your quilt canvas with patches. Be careful! Every patch costs you a few Buttons and it also takes some Time to sew it on your canvas.

income_events = int(input())  # the amount of "Button income" events that will happen
for i in input().split():
    # income_time: when the "Button income" will happen
    income_time = int(i)
patch_events = int(input())  # the amount of "Special Patch" events that will happen
for i in input().split():
    # patch_time: when the "Special Patch" will happen
    patch_time = int(i)
patches = int(input())  # how many patches there are in total in a game
for i in range(patches):
    inputs = input().split()
    patch_id = int(inputs[0])
    patch_earning = int(inputs[1])  # how much Buttons you will acquire for this tile when "Button income" timepoint is reached
    patch_button_price = int(inputs[2])  # how much Buttons does it cost to buy this patch
    patch_time_price = int(inputs[3])  # how much time does it take to sew this patch
    patch_shape = inputs[4]  # representation of patch shape "O.O|OOO|O.O" represents H shaped tile

# game loop
while True:
    # my_buttons: how many Buttons you hold right now
    # my_time: where is my time token placed on timeline
    # my_earning: how much will you earn during "Button income" phase with your current quilt board
    my_buttons, my_time, my_earning = [int(i) for i in input().split()]
    for i in range(9):
        line = input()  # represents row of a board board "O....O.." means, 1st and 6th field is covered by patch on this row
    # opponent_buttons: how many Buttons your opponent holds right now
    # opponent_time: where is opponent time token placed on timeline
    # opponent_earning: how much will opponent earn during "Button income" phase with his current quilt board
    opponent_buttons, opponent_time, opponent_earning = [int(i) for i in input().split()]
    for i in range(9):
        line = input()
    playable_patches = int(input())  # number of patches you can play (3 most of the time, 1 in case of "Special Patch" event)
    for i in range(playable_patches):
        inputs = input().split()
        patch_id = int(inputs[0])
        patch_earning = int(inputs[1])
        patch_button_price = int(inputs[2])
        patch_time_price = int(inputs[3])
        patch_shape = inputs[4]

    # Write an action using print
    # To debug: print("Debug messages...", file=sys.stderr, flush=True)

    print("SKIP")
