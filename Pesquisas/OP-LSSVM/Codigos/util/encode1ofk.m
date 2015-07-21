function R = encode1ofk(value,L)
    [m n] = size(value);    
    if(m>1 && n>1)
	disp('The input must be a 1D vector');
	return;
    end

    minV = min(value);
    if(minV<0)
	disp('Negative values are not alowed in value vector');
    	return;
    end

    if(~exist('L'))
	L = max(value);
    end

    m = length(value);
    R = zeros(m,L);
    R(sub2ind(size(R),1:m,value')) = 1;
end
