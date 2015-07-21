clear all;
close all;
clc;

folderPath = '~/Documentos/imagens SMS/';

imagesInfo = dir(folderPath);

figure;
for i=3:length(imagesInfo)
    imagePath = imagesInfo(i).name;
    
    image = imread([folderPath imagePath]);
    
    
    imageRes = convolutionsResult(rgb2gray(image));
    
    
    subplot(2,2,1);
    imshow(image);

    subplot(2,2,2);
    imshow(imageRes);    

    inputdlg('proximo');
end