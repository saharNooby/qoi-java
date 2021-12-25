# Benchmark results

I've used a [set of various images](https://github.com/phoboslab/qoi/issues/69) proposed by QOI author. The set was stripped to a selection of 50 random images, so benchmark completes in 30 minutes.

[Benchmark code](https://github.com/saharNooby/qoi-java-awt/blob/main/src/test/java/me/saharnooby/qoi/benchmark/FormatComparisonBenchmark.java) can be used to produce results for a set of files that are representative of your task.

Max heap size was set to 4 GB.

Time delays represent delay of a single operation for each format, less is better. Sizes represent size of compressed image foe each format, less is better. When percentage is positive, it means QOI did better; otherwise QOI did worse.

Results:

```
OpenJDK Runtime Environment 17.0.1+12
Warm up: 5 seconds, run: 5 seconds
50 files total

Average over all files
Format                        Decode                      Encode                        Size
QOI           4.771 ms                    7.310 ms                      542 KB              
PNG          10.123 ms         +112%     50.068 ms         +584%        624 KB          +15%

pkw_wall05f.png, 256 x 256
Format                        Decode                      Encode                        Size
QOI           1.091 ms                    1.256 ms                      143 KB              
PNG           1.526 ms          +39%      6.943 ms         +452%         99 KB          -30%

pk01_pan_wall02b_s.png, 256 x 512
Format                        Decode                      Encode                        Size
QOI           1.222 ms                    1.686 ms                      118 KB              
PNG           2.179 ms          +78%     10.856 ms         +543%        113 KB           -4%

Fantastic_Contraption_VR_screenshot_05.png, 1200 x 675
Format                        Decode                      Encode                        Size
QOI           7.014 ms                    9.123 ms                      576 KB              
PNG          12.084 ms          +72%     58.951 ms         +546%        599 KB           +4%

mod_labpipe1c.png, 256 x 128
Format                        Decode                      Encode                        Size
QOI           0.385 ms                    0.439 ms                       36 KB              
PNG           0.591 ms          +53%      3.083 ms         +602%         30 KB          -14%

044.png, 1332 x 952
Format                        Decode                      Encode                        Size
QOI          16.242 ms                   22.611 ms                     2096 KB              
PNG          29.778 ms          +83%    153.075 ms         +576%       2762 KB          +31%

pk01_wall03_s.png, 512 x 512
Format                        Decode                      Encode                        Size
QOI           2.472 ms                    2.987 ms                      232 KB              
PNG           4.290 ms          +73%     23.638 ms         +691%        220 KB           -5%

pk_chwall2.png, 256 x 256
Format                        Decode                      Encode                        Size
QOI           0.926 ms                    1.109 ms                       88 KB              
PNG           1.487 ms          +60%      7.279 ms         +556%         87 KB           -1%

bell_PNG53465.png, 1635 x 3000
Format                        Decode                      Encode                        Size
QOI          30.059 ms                   59.711 ms                     3336 KB              
PNG          86.559 ms         +187%    434.734 ms         +628%       4623 KB          +38%

megaphone_PNG112.png, 765 x 1000
Format                        Decode                      Encode                        Size
QOI           1.628 ms                    4.974 ms                       51 KB              
PNG           8.925 ms         +448%     34.580 ms         +595%         32 KB          -37%

hWall07b.png, 256 x 192
Format                        Decode                      Encode                        Size
QOI           0.679 ms                    0.875 ms                       83 KB              
PNG           1.108 ms          +63%      5.241 ms         +499%         65 KB          -22%

pkw_door02b.png, 64 x 128
Format                        Decode                      Encode                        Size
QOI           0.113 ms                    0.148 ms                       14 KB              
PNG           0.229 ms         +102%      1.052 ms         +612%         12 KB          -12%

arma_PNG36.png, 300 x 300
Format                        Decode                      Encode                        Size
QOI           1.105 ms                    1.398 ms                      141 KB              
PNG           2.329 ms         +110%     13.327 ms         +853%        159 KB          +12%

plant_52.png, 1024 x 1249
Format                        Decode                      Encode                        Size
QOI           4.874 ms                   11.311 ms                      458 KB              
PNG          20.968 ms         +330%     78.472 ms         +593%        549 KB          +19%

mod_labpipe1b.png, 256 x 128
Format                        Decode                      Encode                        Size
QOI           0.409 ms                    0.425 ms                       36 KB              
PNG           0.667 ms          +62%      3.147 ms         +640%         31 KB          -15%

mod_labcab2a.png, 128 x 256
Format                        Decode                      Encode                        Size
QOI           0.125 ms                    0.149 ms                        7 KB              
PNG           0.367 ms         +194%      1.472 ms         +887%          5 KB          -25%

024.png, 1038 x 1072
Format                        Decode                      Encode                        Size
QOI          14.144 ms                   18.940 ms                     1985 KB              
PNG          25.646 ms          +81%    135.628 ms         +616%       2355 KB          +18%

Contraption_1.png, 1200 x 675
Format                        Decode                      Encode                        Size
QOI           5.626 ms                    9.598 ms                      519 KB              
PNG          11.751 ms         +108%     57.745 ms         +501%        566 KB           +9%

Flinthook_screenshot_-_Pax_square_3.png, 800 x 800
Format                        Decode                      Encode                        Size
QOI           2.012 ms                    2.271 ms                       94 KB              
PNG           4.171 ms         +107%     22.363 ms         +884%         58 KB          -38%

Neverball_Gameplay_Screenshot_4.png, 1200 x 675
Format                        Decode                      Encode                        Size
QOI           9.058 ms                   12.671 ms                     1020 KB              
PNG          17.391 ms          +91%     94.541 ms         +646%       1297 KB          +27%

pkw_winlay10a.png, 256 x 256
Format                        Decode                      Encode                        Size
QOI           0.214 ms                    0.271 ms                       13 KB              
PNG           0.608 ms         +183%      2.547 ms         +840%          7 KB          -40%

RGB_OR_1200x1200_047.png, 1200 x 1200
Format                        Decode                      Encode                        Size
QOI          18.335 ms                   25.669 ms                     3550 KB              
PNG          33.951 ms          +85%    185.812 ms         +623%       3685 KB           +3%

mimetypes-video-x-generic.png, 512 x 512
Format                        Decode                      Encode                        Size
QOI           1.090 ms                    2.084 ms                       82 KB              
PNG           3.126 ms         +186%     13.123 ms         +529%         58 KB          -29%

plant_07.png, 1024 x 1450
Format                        Decode                      Encode                        Size
QOI           4.856 ms                   10.634 ms                      574 KB              
PNG          22.478 ms         +362%     79.471 ms         +647%        558 KB           -2%

pk02_wall_big01b_N.png, 1024 x 1024
Format                        Decode                      Encode                        Size
QOI          12.043 ms                   16.582 ms                     1037 KB              
PNG          20.797 ms          +72%    117.460 ms         +608%       1033 KB            0%

towel_PNG64.png, 560 x 481
Format                        Decode                      Encode                        Size
QOI           1.999 ms                    3.440 ms                      337 KB              
PNG           6.346 ms         +217%     31.667 ms         +820%        418 KB          +24%

pk02_wall04b_S.png, 512 x 512
Format                        Decode                      Encode                        Size
QOI           3.557 ms                    4.368 ms                      378 KB              
PNG           5.353 ms          +50%     26.197 ms         +499%        336 KB          -10%

pk02_computer01a_S.png, 256 x 512
Format                        Decode                      Encode                        Size
QOI           1.534 ms                    1.942 ms                      148 KB              
PNG           2.344 ms          +52%     11.660 ms         +500%        128 KB          -13%

This_Is_the_Police_-_Screenshot_04.png, 1200 x 675
Format                        Decode                      Encode                        Size
QOI           4.431 ms                    5.269 ms                      344 KB              
PNG           8.174 ms          +84%     36.969 ms         +601%        297 KB          -13%

pk01_wall04a_s.png, 256 x 512
Format                        Decode                      Encode                        Size
QOI           1.451 ms                    1.713 ms                      144 KB              
PNG           2.476 ms          +70%     12.123 ms         +607%        142 KB           -1%

actions-format-justify-left.png, 512 x 512
Format                        Decode                      Encode                        Size
QOI           0.669 ms                    1.640 ms                       30 KB              
PNG           2.238 ms         +234%     11.237 ms         +585%         22 KB          -27%

Meritous-automap.png, 640 x 480
Format                        Decode                      Encode                        Size
QOI           1.204 ms                    1.527 ms                       73 KB              
PNG           2.075 ms          +72%     11.780 ms         +671%         29 KB          -60%

pkw_barrel02a.png, 128 x 128
Format                        Decode                      Encode                        Size
QOI           0.247 ms                    0.325 ms                       28 KB              
PNG           0.412 ms          +66%      1.887 ms         +480%         23 KB          -16%

pkf_wall4f.png, 256 x 256
Format                        Decode                      Encode                        Size
QOI           0.913 ms                    1.138 ms                       90 KB              
PNG           1.398 ms          +53%      6.820 ms         +499%         75 KB          -16%

pkw_winlay04b.png, 256 x 256
Format                        Decode                      Encode                        Size
QOI           0.208 ms                    0.263 ms                        9 KB              
PNG           0.671 ms         +222%      2.623 ms         +897%          4 KB          -47%

actions-bookmark-new.png, 512 x 512
Format                        Decode                      Encode                        Size
QOI           1.223 ms                    2.563 ms                      120 KB              
PNG           3.413 ms         +179%     15.207 ms         +493%         79 KB          -34%

RGB_OR_1200x1200_003.png, 1200 x 1200
Format                        Decode                      Encode                        Size
QOI          20.535 ms                   28.348 ms                     2337 KB              
PNG          36.036 ms          +75%    188.176 ms         +563%       3038 KB          +29%

Push_Me_Pull_You_screenshot_07.png, 1200 x 675
Format                        Decode                      Encode                        Size
QOI           2.554 ms                    6.283 ms                      132 KB              
PNG           7.763 ms         +203%     39.954 ms         +535%        129 KB           -2%

Trashmania_3.png, 640 x 420
Format                        Decode                      Encode                        Size
QOI           0.723 ms                    0.859 ms                       17 KB              
PNG           2.337 ms         +223%      9.575 ms        +1014%          7 KB          -55%

{SW_Tree06.png, 256 x 256
Format                        Decode                      Encode                        Size
QOI           0.377 ms                    0.422 ms                       25 KB              
PNG           0.729 ms          +93%      3.504 ms         +729%         20 KB          -17%

devices-drive-harddisk.png, 64 x 64
Format                        Decode                      Encode                        Size
QOI           0.031 ms                    0.042 ms                        3 KB              
PNG           0.080 ms         +154%      0.385 ms         +808%          2 KB          -11%

pkw_wall03e.png, 256 x 256
Format                        Decode                      Encode                        Size
QOI           1.029 ms                    1.311 ms                      132 KB              
PNG           1.486 ms          +44%      7.662 ms         +484%         90 KB          -31%

mod_screen1j.png, 64 x 64
Format                        Decode                      Encode                        Size
QOI           0.047 ms                    0.061 ms                        8 KB              
PNG           0.116 ms         +147%      0.507 ms         +737%          5 KB          -26%

pkh_window01a.png, 128 x 256
Format                        Decode                      Encode                        Size
QOI           0.480 ms                    0.658 ms                       83 KB              
PNG           1.179 ms         +145%      5.523 ms         +739%         83 KB           +0%

spinner_PNG17.png, 1024 x 1024
Format                        Decode                      Encode                        Size
QOI           4.662 ms                    9.507 ms                      320 KB              
PNG          15.319 ms         +228%     59.616 ms         +527%        336 KB           +4%

{blaztree2.png, 256 x 256
Format                        Decode                      Encode                        Size
QOI           0.654 ms                    0.834 ms                       87 KB              
PNG           1.080 ms          +65%      5.285 ms         +533%         59 KB          -31%

036.png, 1086 x 858
Format                        Decode                      Encode                        Size
QOI          12.049 ms                   18.415 ms                     2168 KB              
PNG          22.856 ms          +89%    120.758 ms         +555%       2296 KB           +5%

emblems-emblem-favorite.png, 64 x 64
Format                        Decode                      Encode                        Size
QOI           0.030 ms                    0.041 ms                        4 KB              
PNG           0.087 ms         +192%      0.395 ms         +853%          3 KB          -17%

Rain_World_screenshot_-_2016-05-31_16_15_34_climbing.png, 1200 x 675
Format                        Decode                      Encode                        Size
QOI          10.426 ms                   15.358 ms                      876 KB              
PNG          17.108 ms          +64%     98.454 ms         +541%        939 KB           +7%

Absolver_screenshot_Fight01.png, 1200 x 675
Format                        Decode                      Encode                        Size
QOI          10.740 ms                   15.797 ms                     1126 KB              
PNG          19.350 ms          +80%     95.746 ms         +506%       1489 KB          +32%

RGB_OR_1200x1200_002.png, 1200 x 1200
Format                        Decode                      Encode                        Size
QOI          21.061 ms                   26.468 ms                     1807 KB              
PNG          32.718 ms          +55%    155.173 ms         +486%       2158 KB          +19%
```
