package com.pranav.smartfarming.ui.main.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pranav.smartfarming.databinding.RvItemSoilSampleBinding

class SamplesAdapter(
    private val listener: OnSampleClickListener,
    private val list: List<SamplesFragment.SoilSampleDataNameDate>
) :
    RecyclerView.Adapter<SamplesAdapter.SamplesViewHolder>() {


    inner class SamplesViewHolder(val binding: RvItemSoilSampleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: SamplesFragment.SoilSampleDataNameDate) {
            binding.sampleName.text = data.name
            binding.sampleTakenDate.text = data.date
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SamplesViewHolder {
        val binding =
            RvItemSoilSampleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SamplesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SamplesViewHolder, position: Int) {
        holder.bind(list[position])

        holder.itemView.setOnClickListener {
            listener.onSampleItemClicked(position)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnSampleClickListener {
        fun onSampleItemClicked(position: Int)
    }
}