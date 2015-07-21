function R = convolutionsResult(image)
    mask = [-1 -1 -1;
            -1 8 -1;ahua
            
            -1 -1 -1];
    
    R = conv2(mask,image);
    
end