/*
for:app model,
author:yangc,
 init_w:��ʼ����Ч��ͼ����ȣ�
 _exc--switch:���������洰�����ſ��أ�
 _exc--max:����ȣ�
 _exc--min:��С��ȣ�
 init_f����ʼ��ҳ���ȣ�Ĭ�϶�html-font;
 */
var resizeFont = {
    _key:{
        init_w:720,
        plam_item:document.getElementsByTagName('html')[0],
        _exc:{
            switch:false,
            max:720,
            min:320
        },
        init_f:0
    },
    extend:function(o,n){
        for(var p in n){
            if(n.hasOwnProperty(p) || !o.hasOwnProperty(p)){
                o[p]=n[p];
            }
        }
    },
    getStyle:function(obj, attr) {
        return obj.currentStyle ? obj.currentStyle[attr] : getComputedStyle(obj,false)[attr];
    },
    data_init:function(){
        var main_var = window.resizeFont, _ths = main_var._key,win_width,init;
        win_width = window.innerWidth;
        var countFont = function(_sig){
            if(_sig){
                init = (_ths.init_f/_ths.init_w)*_sig;
            }else{
                init = (_ths.init_f/_ths.init_w)*win_width;
            }
        }
        countFont();
        if(_ths._exc.switch){
            if(_ths._exc.max && _ths._exc.max<win_width){//���ֵ
                countFont(_ths._exc.max);
            }
            if(_ths._exc.min && _ths._exc.min>win_width){//��Сֵ
                countFont(_ths._exc.min);
            }
        }
        _ths.plam_item.style.fontSize = init+'px';
    },
    init_fn:function (_str){
        _ths = window.resizeFont
        _ths._key.init_f = parseInt(_ths.getStyle(_ths._key.plam_item,'font-size'));
        _ths.extend(_ths._key,_str);
        _ths.data_init();
        if(document.all){
            window.attachEvent('resize',function(){
                _ths.data_init();
            })
        }else{
            window.addEventListener('resize',function(){
                _ths.data_init();
            },false)
        }
    }
}

