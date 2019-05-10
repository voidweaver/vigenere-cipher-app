package com.voidcode.vigenere_cipher;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class frag_dec extends Fragment {

    Button decrypt, options;
    EditText input, key, output;

    Button copy_in, paste_in, copy_out, paste_out;

    Switch ws, restart, custom;
    EditText alpha;

    boolean[] isSelected = {true, false, false};
    String custom_alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    Cipher cipher = new Cipher(isSelected[0], isSelected[1], custom_alphabet);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.frag_dec, container, false);

        decrypt = view.findViewById(R.id.decrypt);
        options = view.findViewById(R.id.options_d);

        input = view.findViewById(R.id.input_d);
        key = view.findViewById(R.id.key_d);
        output = view.findViewById(R.id.output_d);

        copy_in = view.findViewById(R.id.copy_in_d);
        copy_out = view.findViewById(R.id.copy_out_d);
        paste_in = view.findViewById(R.id.paste_in_d);
        paste_out = view.findViewById(R.id.paste_out_d);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input_text = input.getText().toString();

                String key_str = key.getText().toString();

                String result = "";

                if(!key_str.equals("")) result = cipher.decrypt(input_text, key_str);

                output.setText(result);
            }
        });

        key.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        key.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int selection = key.getSelectionEnd();
                String regex = "[^" + custom_alphabet + "]";
                String result = s.toString().replaceAll(regex, "");
                if (!s.toString().equals(result)) {
                    key.setText(result);
                    if (result.length() < selection) key.setSelection(result.length());
                    else key.setSelection(selection);
                }
            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle(R.string.options);

                View view = getLayoutInflater().inflate(R.layout.options_dialog, null);

                final ArrayList<Switch> switches = new ArrayList<>();

                switches.add(ws = view.findViewById(R.id.skip_punc));
                switches.add(restart = view.findViewById(R.id.restart));
                switches.add(custom = view.findViewById(R.id.custom));

                alpha = view.findViewById(R.id.alphabet);

                alpha.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

                builder.setView(view);

                if(isSelected[0]) ws.setChecked(true);
                if(isSelected[1]) restart.setChecked(true);
                if(isSelected[2]){
                    custom.setChecked(true);
                    alpha.setVisibility(View.VISIBLE);
                    alpha.setText(custom_alphabet);
                }else{
                    alpha.setVisibility(View.GONE);
                    alpha.setText(getResources().getString(R.string.default_alphabet));
                }

                final boolean[] isSelectedNew = new boolean[isSelected.length];

                custom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(switches.get(2).isChecked()) alpha.setVisibility(View.VISIBLE);
                        else alpha.setVisibility(View.GONE);
                    }
                });

                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(int i = 0; i < 3; i++){
                            isSelectedNew[i] = switches.get(i).isChecked();
                        }
                        if(alpha.getVisibility() == View.VISIBLE){
                            custom_alphabet = alpha.getText().toString();
                        }else{
                            custom_alphabet = getResources().getString(R.string.default_alphabet);
                        }
                        isSelected = isSelectedNew;
                        cipher = new Cipher(isSelected[0], isSelected[1], custom_alphabet);

                        String key_str = key.getText().toString();
                        String regex = "[^" + custom_alphabet + "]";
                        key.setText(key_str.replaceAll(regex, ""));
                    }
                });

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();

                dialog.setCanceledOnTouchOutside(true);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        for(int i = 0; i < 3; i++){
                            isSelectedNew[i] = switches.get(i).isChecked();
                        }
                        if(alpha.getVisibility() == View.VISIBLE){
                            custom_alphabet = alpha.getText().toString();
                        }else{
                            custom_alphabet = getResources().getString(R.string.default_alphabet);
                        }
                        isSelected = isSelectedNew;
                        cipher = new Cipher(isSelected[0], isSelected[1], custom_alphabet);

                        String key_str = key.getText().toString();
                        String regex = "[^" + custom_alphabet + "]";
                        key.setText(key_str.replaceAll(regex, ""));
                    }
                });

                dialog.show();
            }
        });

        copy_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                String text = input.getText().toString();

                if (!text.equals("")) {
                    ClipData clip = ClipData.newPlainText("text", text);
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(getActivity(), getResources().getString(R.string.copy_success), Toast.LENGTH_SHORT).show();
                }
            }
        });

        copy_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                String text = output.getText().toString();

                if (!text.equals("")) {
                    ClipData clip = ClipData.newPlainText("text", text);
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(getActivity(), getResources().getString(R.string.copy_success), Toast.LENGTH_SHORT).show();
                }
            }
        });

        paste_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

                if (clipboard.hasPrimaryClip() && Objects.requireNonNull(clipboard.getPrimaryClipDescription()).hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);

                    input.setText(item.getText().toString());
                }else Toast.makeText(getActivity(), getResources().getString(R.string.paste_fail), Toast.LENGTH_SHORT).show();
            }
        });

        paste_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

                if (clipboard.hasPrimaryClip() && Objects.requireNonNull(clipboard.getPrimaryClipDescription()).hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);

                    output.setText(item.getText().toString());
                }else Toast.makeText(getActivity(), getResources().getString(R.string.paste_fail), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String removeDuplicate(String s){
        String exist = "";
        for(int i = 0; i<s.length(); i++){
            char c = s.charAt(i);
            if(!exist.contains(String.valueOf(c))){
                exist += c;
            }
        }
        return exist;
    }
}
