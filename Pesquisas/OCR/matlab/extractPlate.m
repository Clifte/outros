clear all;
close all;
clc;
%63



folderPath = '~/Documentos/imagens SMS/';

imagesInfo = dir(folderPath);

options.Resize='on';
options.WindowStyle='normal';
options.Interpreter='none';
defAns = {'ans'};

figure;
mkdir('plates');


i0 = inputdlg('Digite o indice.');
i0 = cell2mat(i0);
i0 = str2num(i0);  

for i=(i0+3):length(imagesInfo)
    
        
    imagePath = imagesInfo(i).name;
    
    image = imread([folderPath imagePath]);
    imshow(image);
    title(sprintf('%d',i));
 
 
 
    h = imrect
    pos = getPosition(h)
    
    placa = inputdlg('Digite a placa.', ...
                     'Digite a placa', ...
                    1, ...
                    {'AAA0000'}, ...
                    options);
    placa = cell2mat(placa);
    
    rangey = floor(pos(1)):floor(pos(1) + pos(3));
    rangex = floor(pos(2)):floor(pos(2) + pos(4));
    imwrite(image(rangex,rangey),[ 'plates/' placa '.jpeg']);


end