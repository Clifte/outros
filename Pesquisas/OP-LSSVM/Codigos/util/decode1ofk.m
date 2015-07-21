function D = decode1ofk(value)
    [v D] = max(value');
    D = D';
end